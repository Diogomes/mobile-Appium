# A Hierarquia do Projeto do Ponto de Vista das Boas Práticas

> Este texto explica **porque** o projeto está organizado como está. Mais do que "onde ficam os ficheiros", o objetivo é mostrar como cada nível da hierarquia serve um princípio de engenharia — e o que ganhamos (e evitamos) com isso.

---

## A ideia central: cada nível tem uma única responsabilidade

Um bom framework de automação não é "uma pasta de testes". É um sistema em **camadas**, onde cada camada só conhece a de baixo e expõe uma interface simples à de cima. Quando isto é respeitado, uma mudança numa ponta (ex: um botão mudou de id) não se propaga em cascata pelo resto do código.

```
              ╭───────────────────────────────────────────╮
   ALTO       │  TESTES        — intenção de negócio        │   "o quê"
   NÍVEL      │  (tests/, providers/, suites/)              │
              ├───────────────────────────────────────────┤
              │  PÁGINAS       — tradução UI ↔ negócio      │
              │  (pages/)                                   │
              ├───────────────────────────────────────────┤
              │  CORE          — mecânica de automação      │
   BAIXO      │  (driver/, config/, utils/, data/)         │   "como"
   NÍVEL      ╰───────────────────────────────────────────╯
```

A regra de dependência é **só para baixo**: os testes dependem das páginas, as páginas dependem do core, o core não depende de ninguém acima. Nunca o contrário. Isto é o que mantém o sistema sustentável à medida que cresce.

---

## Nível 1 — Maven: `src/main` vs `src/test`

A primeira separação é estrutural e vem da convenção Maven:

- **`src/main/java`** → o **framework** reutilizável: driver, páginas, utilitários, modelos de dados. É código de produção do ponto de vista do projeto de automação — pode ser empacotado e partilhado entre repositórios.
- **`src/test/java`** → os **testes** e a sua orquestração: classes de teste, listeners, data providers, base do ciclo de vida.
- **`src/test/resources`** → tudo o que não é código: configuração (`.properties`), dados (`.json`), suites (`.xml`), logging.

**Porque isto é boa prática:** estabelece um limite claro entre "a ferramenta" e "o uso da ferramenta". Quem escreve novos testes mexe em `src/test`; quem evolui o framework mexe em `src/main`. Os dois evoluem sem se pisar.

---

## Nível 2 — Pacotes: agrupar por responsabilidade técnica

Dentro de `src/main`, os pacotes **não** estão organizados por ecrã ou por feature, mas por **responsabilidade**:

| Pacote | Responsabilidade | Princípio servido |
|---|---|---|
| `driver/` | Criar e gerir a sessão Appium | _Single Responsibility_ + Factory |
| `config/` | Ler e expor configuração | Externalização da configuração |
| `pages/` | Representar ecrãs (POM) | _Page Object Model_ |
| `utils/` | Ações reutilizáveis (esperas, gestos) | DRY (não te repitas) |
| `data/` | Modelar e ler dados de teste | _Data-driven_ |

**Porque isto é boa prática:** quando preciso de mexer em "como esperamos por elementos", sei imediatamente que é `utils/WaitUtils`. Quando um driver precisa de uma nova _capability_, é `driver/DriverFactory`. A localização do código é **previsível** — qualquer pessoa nova na equipa orienta-se em minutos.

---

## Nível 3 — A hierarquia de classes (herança vs composição)

Aqui há duas decisões deliberadas que vale a pena destacar.

### 3.1. Herança onde modela um "é-um" real

```
BasePage  ←  LoginPage, HomePage, ProductsPage, CartPage, ...
BaseTest  ←  LoginTest, PurchaseFlowTest, DataDrivenLoginTest, ...
```

- **`BasePage`** concentra o que **todas** as páginas precisam: o driver da thread, a inicialização do PageFactory e os _helpers_ `type/click/readText/isDisplayed`. Cada página concreta só adiciona os seus elementos e métodos de negócio.
- **`BaseTest`** concentra o **ciclo de vida**: `@BeforeMethod` cria a sessão, `@AfterMethod` encerra-a. Cada teste concreto herda isto e foca-se apenas no cenário.

*Porquê herança aqui:* "uma LoginPage **é uma** página" e "um LoginTest **é um** teste" são relações genuínas de especialização. A herança elimina dezenas de linhas repetidas de _setup_ em cada classe.

### 3.2. Composição e estática onde NÃO há estado partilhado

- **`WaitUtils`, `GestureUtils`, `ScreenshotUtils`, `DriverFactory`** são classes utilitárias com **construtor privado** e métodos estáticos. Não têm estado; são funções puras de apoio.
- As páginas **compõem** utilitários (chamam `GestureUtils.swipeUp(...)`) em vez de herdar deles.

*Porquê:* herdar de um utilitário só para "ter acesso" aos seus métodos seria abuso de herança. Composição mantém as responsabilidades separadas e testáveis.

---

## Nível 4 — O fluxo de controlo e o porquê do `ThreadLocal`

A hierarquia também se vê no **fluxo de execução**, não só nas pastas:

```
Suite TestNG (.xml)
   └─ define platform + regista TestListener
       └─ BaseTest.setUp()                 cria a sessão
           └─ DriverFactory.create()       escolhe Android/iOS
               └─ DriverManager.set()      guarda no ThreadLocal da thread
                   └─ TESTE usa Page Objects
                       └─ BasePage usa DriverManager.get()   (mesma thread)
       └─ BaseTest.tearDown()              encerra + limpa o ThreadLocal
```

O ponto subtil e mais importante de boas práticas aqui é o **`DriverManager` com `ThreadLocal`**:

- Se o driver fosse uma variável **estática partilhada**, dois testes em paralelo escreveriam no mesmo dispositivo e baralhavam-se.
- Com `ThreadLocal`, **cada thread tem o seu driver isolado**. A suite `testng-parallel.xml` corre Android e iOS ao mesmo tempo, em segurança.
- O `tearDown` faz `DRIVER.remove()` — **essencial**, porque as threads do _pool_ do TestNG são reutilizadas; deixar um driver morto agarrado causaria fugas de memória e sessões fantasma.

Esta é a diferença entre um framework que "funciona na minha máquina com um teste" e um que escala para uma suite paralela em CI.

---

## Nível 5 — Configuração e dados fora do código

A última camada da hierarquia são os recursos, e a regra é: **o que muda por ambiente ou por cenário não fica no código compilado**.

- **`config/*.properties`** — endereços, dispositivos, _timeouts_. Sobreponíveis por `-Dchave=valor`. *Porquê:* o mesmo `.jar` corre em local, no portátil de um colega e em CI sem recompilar.
- **`testdata/*.json`** — utilizadores, produtos, moradas. *Porquê:* adicionar um cenário data-driven é editar JSON, não Java.
- **`suites/*.xml`** — que testes correr, em que plataforma, em série ou paralelo. *Porquê:* a estratégia de execução é configuração, não código.

A **precedência** (`-D` > ficheiro da plataforma > ficheiro comum) é uma boa prática deliberada: há um valor base versionado no repositório, mas qualquer ambiente pode sobrepô-lo sem editar ficheiros.

---

## Resumo: que princípios a hierarquia materializa

| Princípio | Como a estrutura o garante |
|---|---|
| **Separação de responsabilidades** | Camadas core / páginas / testes; pacotes por responsabilidade |
| **DRY (não repetir)** | `BasePage`/`BaseTest` + utilitários partilhados |
| **Single Responsibility** | Cada classe faz uma coisa (Factory cria, Manager guarda, Page representa) |
| **Inversão de detalhe** | O teste fala de negócio; a mecânica está escondida em baixo |
| **Configuração externalizada** | `.properties` / `.json` / `.xml` fora do código |
| **Segurança em paralelo** | `ThreadLocal` no `DriverManager` |
| **Fail-fast** | `ConfigManager.get()` falha cedo com mensagem clara |
| **Previsibilidade** | Localização do código é óbvia → onboarding rápido |

---

## Em uma frase

> A hierarquia existe para que **uma mudança fique contida no seu nível**: trocar um locator mexe numa página, mudar de dispositivo mexe num `.properties`, adicionar um caso mexe num `.json`, e correr em paralelo não mexe em nada — porque o desenho já o previu.
