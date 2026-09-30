# Mobile Appium Framework — Automação Cross-Platform (Android + iOS)

Framework de automação de testes mobile construído com **Java + Appium + TestNG + Maven**, seguindo boas práticas profissionais: **Page Object Model**, gestão de driver **thread-safe**, configuração externalizada, relatórios **Allure** e suporte a **execução paralela**.

> Este repositório serve de base para automação real com Appium e de material de apoio ao artigo do blog. Cada classe está comentada a explicar *o quê* e, sobretudo, *porquê*.

### 📚 Documentação
- **[docs/CENARIOS.md](docs/CENARIOS.md)** — cada cenário de teste, as funções usadas e o porquê.
- **[docs/HIERARQUIA-E-BOAS-PRATICAS.md](docs/HIERARQUIA-E-BOAS-PRATICAS.md)** — como funciona a hierarquia do projeto do ponto de vista de boas práticas.

---

## 📐 Arquitetura em camadas

A regra de ouro é a **separação de responsabilidades**: cada camada tem um trabalho e não conhece os detalhes das outras.

```
┌─────────────────────────────────────────────────────────┐
│  TESTES (src/test/.../tests)                             │
│  Falam a linguagem do utilizador. Sem locators.          │
│  "login com sucesso", "logout volta ao login"            │
└───────────────────────────┬─────────────────────────────┘
                            │ usa
┌───────────────────────────▼─────────────────────────────┐
│  PAGE OBJECTS (src/main/.../pages)                       │
│  Um ecrã = uma classe. Métodos de negócio + locators     │
│  cross-platform (@AndroidFindBy / @iOSXCUITFindBy)       │
└───────────────────────────┬─────────────────────────────┘
                            │ usa
┌───────────────────────────▼─────────────────────────────┐
│  CORE (driver / config / utils)                          │
│  DriverFactory · DriverManager (ThreadLocal) ·           │
│  ConfigManager · Waits · Gestures · Screenshots          │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Estrutura do projeto

```
mobile-Appium/
├── pom.xml                          # Dependências + plugins (Surefire, Allure)
├── README.md
├── .gitignore
│
├── src/main/java/com/blog/appium/
│   ├── config/
│   │   └── ConfigManager.java       # Lê .properties; -D sobrepõe ficheiro
│   ├── driver/
│   │   ├── Platform.java            # Enum ANDROID/IOS (sem strings mágicas)
│   │   ├── DriverFactory.java       # Cria o driver certo p/ cada plataforma
│   │   └── DriverManager.java       # Driver por thread (ThreadLocal)
│   ├── pages/
│   │   ├── BasePage.java            # Base do POM (PageFactory + helpers)
│   │   ├── LoginPage.java           # Exemplo: locators Android + iOS
│   │   └── HomePage.java
│   └── utils/
│       ├── WaitUtils.java           # Esperas explícitas (nada de sleep!)
│       ├── GestureUtils.java        # Swipe/tap via W3C Actions API
│       └── ScreenshotUtils.java     # Evidências anexadas ao Allure
│
└── src/test/
    ├── java/com/blog/appium/
    │   ├── base/BaseTest.java        # Ciclo de vida da sessão (Before/After)
    │   ├── listeners/
    │   │   ├── TestListener.java     # Screenshot automático em falha
    │   │   └── RetryAnalyzer.java    # Re-tenta falhas transitórias
    │   └── tests/
    │       ├── LoginTest.java
    │       └── HomeTest.java
    └── resources/
        ├── config/                   # config + android + ios .properties
        ├── suites/                   # testng-android / ios / parallel .xml
        └── log4j2.xml                # Logging consola + ficheiro
```

**Porque `main` e `test` separados?** O código reutilizável do framework (driver, páginas, utils) vive em `src/main` e pode ser empacotado/partilhado; os testes e suites vivem em `src/test`. É a convenção Maven e mantém limites claros.

---

## 🧱 Padrões e boas práticas aplicadas

| Boa prática | Onde | Porquê |
|---|---|---|
| **Page Object Model** | `pages/` | UI muda num só sítio; testes legíveis |
| **Factory Pattern** | `DriverFactory` | Isola a criação de drivers Android/iOS |
| **ThreadLocal driver** | `DriverManager` | Execução paralela segura |
| **Config externalizada** | `ConfigManager` + `.properties` | Mesmo código, vários ambientes |
| **Esperas explícitas** | `WaitUtils` | Estabilidade sem `Thread.sleep()` |
| **`accessibility id`** | Page Objects | Locator estável e cross-platform |
| **Screenshot em falha** | `TestListener` | Diagnóstico rápido no relatório |
| **Retry controlado** | `RetryAnalyzer` | Reduz falsos negativos transitórios |
| **Fail-fast na config** | `ConfigManager.get()` | Erros claros e cedo, não NPEs tardios |

---

## ⚙️ Pré-requisitos

- **JDK 17+** e **Maven 3.8+**
- **Node.js** + **Appium 2.x**: `npm i -g appium`
- Driver da plataforma:
  - Android: `appium driver install uiautomator2` + Android SDK/emulador
  - iOS (só macOS): `appium driver install xcuitest` + Xcode
- (Opcional) **Allure CLI** para os relatórios

Verificação útil: `appium driver doctor uiautomator2`

---

## ▶️ Como executar

1. **Arrancar o servidor Appium** (noutro terminal):
   ```bash
   appium
   ```

2. **Arrancar emulador/simulador** e confirmar:
   ```bash
   adb devices                 # Android
   xcrun simctl list devices   # iOS
   ```

3. **Ajustar a app** em `src/test/resources/config/android.properties`
   (ou `ios.properties`): define `app.path` **ou** `app.package`/`app.activity`.

4. **Correr os testes**:
   ```bash
   # Android (suite default)
   mvn clean test

   # Escolher suite explicitamente
   mvn test -Dsuite=testng-ios.xml
   mvn test -Dsuite=testng-parallel.xml

   # Cenários complexos (data-driven + compra ponta-a-ponta)
   mvn test -Dsuite=testng-e2e.xml -Dplatform=android

   # Sobrepor configuração sem editar ficheiros
   mvn test -Dplatform=android -Ddevice.name="Pixel_7" -DnoReset=true
   ```

5. **Relatório Allure**:
   ```bash
   mvn allure:serve
   ```

### 🎥 Capturar vídeo da execução

O framework grava o ecrã automaticamente via API nativa do Appium (`startRecordingScreen`/`stopRecordingScreen`). Os `.mp4` ficam em `recordings/` e são anexados ao relatório Allure.

```bash
# Gravar e guardar vídeo só dos testes que falham (default)
mvn test -Dsuite=testng-e2e.xml -Dvideo.enabled=true

# Gravar e guardar vídeo de TODOS os testes (bom para demos/blog)
mvn test -Dsuite=testng-e2e.xml -Dvideo.enabled=true -Dvideo.onlyOnFailure=false
```

> Requer um device/emulador + Appium reais a correr. A gravação é feita no próprio dispositivo, por isso não precisa de ferramentas extra no host.

---

## 🔄 Como tudo se liga (fluxo de uma execução)

1. O Maven/Surefire arranca a **suite TestNG** indicada em `-Dsuite`.
2. A suite define o parâmetro `platform` e regista o `TestListener`.
3. Antes de cada `@Test`, o `BaseTest.setUp()`:
   - resolve a `Platform`,
   - o `ConfigManager` carrega `config.properties` + o `.properties` da plataforma,
   - o `DriverFactory` cria o `AndroidDriver`/`IOSDriver`,
   - o `DriverManager` guarda-o no `ThreadLocal` da thread atual.
4. O **teste** instancia Page Objects e chama métodos de negócio.
5. O `BasePage` usa esperas explícitas e o driver da thread.
6. Se o teste **falhar**, o `TestListener` captura screenshot → Allure (e o `RetryAnalyzer` pode repetir).
7. O `BaseTest.tearDown()` encerra a sessão e **limpa o ThreadLocal**.

---

## 🧪 Dica para o artigo do blog

Boa ordem narrativa para o post:
1. **Problema** — testar manualmente em Android *e* iOS não escala.
2. **POM** — porquê separar testes de locators (mostrar `LoginTest` vs `LoginPage`).
3. **Cross-platform** — um Page Object, dois `@FindBy` (Android + iOS).
4. **ThreadLocal & paralelismo** — porque uma variável estática parte tudo.
5. **Config externalizada** — o mesmo binário a correr em local e CI.
6. **Estabilidade** — esperas explícitas, screenshots e retries.
7. **Relatórios** — Allure como ponte entre QA e negócio.

---

## 📝 Notas

- Os locators (`username_field`, etc.) e credenciais são **exemplos**: substitui pelos `accessibility id` reais da tua app.
- Para CI (GitHub Actions, etc.), basta correr `mvn test -Dsuite=...` apontando para um device farm ou emulador headless.
- Nunca commitar segredos — usa `-D` ou variáveis de ambiente (ver `.gitignore`).
