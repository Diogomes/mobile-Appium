package com.blog.appium.base;

import com.blog.appium.config.ConfigManager;
import com.blog.appium.driver.DriverFactory;
import com.blog.appium.driver.DriverManager;
import com.blog.appium.driver.Platform;
import com.blog.appium.utils.VideoRecorder;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Classe base de todos os testes. Centraliza o ciclo de vida da sessao Appium.
 *
 * <h2>Ciclo de vida (uma sessao por teste)</h2>
 * <ul>
 *   <li>{@code @BeforeMethod} cria um driver novo antes de cada @Test.</li>
 *   <li>{@code @AfterMethod} encerra-o sempre, mesmo que o teste falhe.</li>
 * </ul>
 *
 * <p>Criar uma sessao por teste garante <b>isolamento</b>: cada teste comeca
 * num estado limpo e nao herda lixo de testes anteriores. E mais lento que
 * reutilizar a sessao, mas muito mais fiavel — um trade-off que vale a pena.</p>
 *
 * <p>A plataforma vem do parametro {@code platform} (definido na suite TestNG
 * ou via -Dplatform), permitindo correr a mesma suite em Android ou iOS sem
 * tocar no codigo.</p>
 */
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // Controla a gravacao de video; lido da configuracao no setUp
    private boolean videoEnabled;
    private boolean videoOnlyOnFailure;

    @Parameters("platform")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("android") String platformParam) {
        Platform platform = Platform.from(System.getProperty("platform", platformParam));
        log.info("===== A iniciar sessao | plataforma={} =====", platform);

        ConfigManager config = ConfigManager.forPlatform(platform);
        AppiumDriver driver = DriverFactory.create(config);
        DriverManager.setDriver(driver);

        videoEnabled = config.getBoolean("video.enabled", false);
        videoOnlyOnFailure = config.getBoolean("video.onlyOnFailure", true);
        if (videoEnabled) {
            VideoRecorder.start(driver);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (videoEnabled) {
            boolean failed = result.getStatus() == ITestResult.FAILURE;
            boolean keep = failed || !videoOnlyOnFailure;
            VideoRecorder.stop(DriverManager.getDriver(), result.getName(), keep);
        }
        log.info("===== A encerrar sessao =====");
        DriverManager.quitDriver();
    }
}
