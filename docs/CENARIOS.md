# Cenários de Teste — Funções Utilizadas e o Porquê

Este documento descreve os cenários de automação implementados, as funções/técnicas que cada um usa e a justificação por trás de cada decisão. Serve de guia de leitura do código e de material de apoio ao artigo do blog.

---

## Índice

1. [Login simples (caminho feliz e erro)](#1-login-simples)
2. [Login data-driven (vários cenários a partir de JSON)](#2-login-data-driven)
3. [Fluxo de compra ponta-a-ponta (E2E)](#3-fluxo-de-compra-ponta-a-ponta)
4. [Catálogo de carrinho vazio](#4-carrinho-vazio)
5. [Catálogo de funções de suporte](#catálogo-de-funções-de-suporte)

---

## 1. Login simples

**Ficheiro:** `tests/LoginTest.java` · **Páginas:** `LoginPage`, `HomePage`

| Cenário | O que valida |
|---|---|
| `loginComSucesso` | Credenciais válidas → ecrã inicial visível |
| `loginComCredenciaisInvalidas` | Credenciais erradas → mensagem de erro |

**Funções usadas e porquê:**

- **`loginAs(user, pass)` / `loginExpectingFailure(...)`** — métodos de negócio no Page Object. O teste não vê `sendKeys` nem `click`; lê-se como uma frase. *Porquê:* esconder a mecânica torna o teste estável a mudanças de UI.
- **`type()` / `click()`** (em `BasePage`) — encapsulam espera + ação. *Porquê:* garantem que o elemento está visível/clicável antes de interagir, eliminando falhas de _timing_.
- **`@Severity` / `@Description`** (Allure) — classificam o teste por criticidade de negócio. *Porquê:* o relatório passa a comunicar risco, não só "passou/falhou".
- **`retryAnalyzer = RetryAnalyzer.class`** — repete falhas transitórias. *Porquê:* reduz falsos negativos em ambiente mobile (rede, emulador a aquecer).

---

## 2. Login data-driven

**Ficheiro:** `tests/DataDrivenLoginTest.java` · **Dados:** `resources/testdata/users.json`

Um **único** método de teste cobre 4 cenários (válido, inválido, bloqueado, campos vazios) lidos de JSON. Adicionar um caso novo = acrescentar uma entrada no JSON, **sem código novo**.

**Funções usadas e porquê:**

- **`@DataProvider(name = "users")`** (`providers/DataProviders.java`) — alimenta o teste com uma linha por execução. *Porquê:* o TestNG cria N execuções independentes; cada falha é isolada e reportada por dados.
- **`TestDataReader.readList("testdata/users.json", User.class)`** — desserializa JSON em objetos tipados via Jackson. *Porquê:* separar dados do código permite alterar/expandir cenários sem recompilar e partilhar dados entre testes.
- **`record User(...)`** — modelo fortemente tipado. *Porquê:* `user.username()` é auto-documentado; evita o anti-padrão `data[0]`, `data[1]`.
- **`@JsonIgnoreProperties(ignoreUnknown = true)`** — torna o parsing resiliente a campos extra. *Porquê:* o JSON pode evoluir sem quebrar testes existentes.

> **Boa prática:** os dados descrevem *também* o resultado esperado (`shouldSucceed`, `expectedError`). Assim o mesmo método valida sucesso e erro, mantendo um só ponto de asserção por tipo.

---

## 3. Fluxo de compra ponta-a-ponta

**Ficheiro:** `tests/PurchaseFlowTest.java` (`compraCompletaComSucesso`)
**Páginas:** `LoginPage → HomePage → ProductsPage → ProductDetailsPage → CartPage → CheckoutPage`

O cenário mais complexo: **6 ecrãs**, pesquisa, scroll, formulário e verificação final. Apesar disso, o teste lê-se como uma jornada de utilizador porque toda a complexidade vive nos Page Objects.

**Passos e funções:**

| Passo | Função | Porquê |
|---|---|---|
| 1. Login | `loginAs(...)` | Reutiliza o Page Object já existente (reuso) |
| 2. Abrir catálogo | `home.openProducts()` | Navegação encadeada que devolve a próxima página |
| 3. Pesquisar + abrir produto | `search(term).openProduct(name)` | **Locator dinâmico** + **scroll automático** |
| 4. Adicionar ao carrinho | `addToCart().goBack()` | _Fluent API_: encadeia ações do mesmo ecrã |
| 5. Validar badge | `getCartCount()` | Lê estado da UI e compara com o esperado |
| 6. Carrinho | `openCart()` + `getItemCount()` | **`findElements`** conta linhas sem rebentar |
| 7. Checkout | `fillShippingDetails(...).confirmOrder()` | Formulário multi-campo + scroll até ao botão |
| 8. Verificação | `isOrderConfirmed()` | Asserção final do resultado de negócio |

**Técnicas-chave deste cenário:**

- **Locator dinâmico** — em `ProductsPage.openProduct()`, o `accessibility id` é construído em runtime (`"product_item_" + nome`). *Porquê:* o produto é um dado, não um valor fixo; combinar id + dado de forma determinística evita XPath frágil.
- **`GestureUtils.swipeUpUntilVisible(driver, element, maxSwipes)`** — faz scroll até o item entrar no ecrã. *Porquê:* em listas longas o elemento pode nem existir na árvore de acessibilidade até ser scrollado. Devolve `boolean` para o Page Object decidir o que fazer se não aparecer.
- **`findElements(AppiumBy.accessibilityId("cart_item_row"))`** — devolve uma **lista** (vazia se nada). *Porquê:* ideal para contar/iterar; ao contrário de `findElement`, não lança exceção quando há zero resultados.
- **Page Objects que devolvem a página seguinte** — `proceedToCheckout()` devolve `CheckoutPage`. *Porquê:* modela a navegação real da app e dá autocompletar no IDE para o passo seguinte.

---

## 4. Carrinho vazio

**Ficheiro:** `tests/PurchaseFlowTest.java` (`carrinhoIniciaVazio`)

Cenário curto que valida o **estado inicial**: um utilizador novo tem o carrinho vazio.

- **`isEmpty()` / `isDisplayed(emptyMessage)`** — verificação tolerante (try/catch interno) que devolve `boolean` em vez de lançar exceção. *Porquê:* "ausência de itens" é um estado esperado, não um erro de teste.

---

## Catálogo de funções de suporte

Funções transversais reutilizadas pelos cenários, e a razão de existirem:

### Esperas (`WaitUtils`)
- `waitForVisibility` / `waitForClickable` / `waitForInvisibility` — **esperas explícitas**.
  *Porquê:* nunca usar `Thread.sleep()`. Esperam por uma _condição_ até um limite → testes simultaneamente mais rápidos (avançam logo que a condição é satisfeita) e mais estáveis.

### Gestos (`GestureUtils`)
- `swipeUp` / `swipeDown` — swipe vertical por **percentagem do ecrã** (W3C Actions API).
  *Porquê:* percentagens tornam o gesto independente da resolução; a W3C Actions API substitui a antiga `TouchAction` (descontinuada) e funciona igual em Android e iOS.
- `tap` — toque por coordenadas.
- `swipeUpUntilVisible` — scroll até encontrar (ver cenário 3).

### Evidências (`ScreenshotUtils`)
- `attachToAllure` — anexa screenshot ao relatório.
  *Porquê:* invocado automaticamente pelo `TestListener.onTestFailure`; quem analisa o relatório vê logo o estado do ecrã no momento do erro.

### Configuração (`ConfigManager`)
- `get` / `getInt` / `getBoolean` — leem `.properties` com precedência `-D` > plataforma > comum.
  *Porquê:* o mesmo binário corre em local e em CI sem editar ficheiros; `get(key)` faz _fail-fast_ se uma chave obrigatória faltar.

### Ciclo de vida (`BaseTest`, `DriverFactory`, `DriverManager`)
- `setUp` / `tearDown` — criam e encerram **uma sessão por teste**.
  *Porquê:* isolamento — cada teste começa limpo e não herda lixo do anterior.
- `DriverManager` (ThreadLocal) — driver por thread.
  *Porquê:* permite execução paralela segura (ver `testng-parallel.xml`).

---

## Como correr cada cenário

```bash
# Login simples (suite default)
mvn test -Dsuite=testng-android.xml

# Cenários complexos (data-driven + compra E2E)
mvn test -Dsuite=testng-e2e.xml -Dplatform=android

# Mesmos testes em iOS
mvn test -Dsuite=testng-e2e.xml -Dplatform=ios

# Relatório visual
mvn allure:serve
```

> ⚠️ Os `accessibility id` (`product_item_*`, `cart_item_row`, etc.) e os dados em `users.json` são **exemplos**. Substitui pelos identificadores reais da tua app.
