package com.blog.appium.driver;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Guarda o {@link AppiumDriver} da thread atual usando {@link ThreadLocal}.
 *
 * <h2>Porque ThreadLocal?</h2>
 * <p>O TestNG pode correr testes <b>em paralelo</b>, cada um na sua thread.
 * Se o driver fosse uma variavel estatica partilhada, duas threads escreveriam
 * no mesmo dispositivo e os testes baralhavam-se entre si. Com ThreadLocal,
 * cada thread tem o <i>seu</i> driver isolado — esta e a forma padrao de tornar
 * um framework Appium/Selenium seguro para execucao paralela.</p>
 */
public final class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void setDriver(AppiumDriver driver) {
        DRIVER.set(driver);
    }

    /**
     * Devolve o driver da thread atual.
     *
     * @throws IllegalStateException se nenhum driver tiver sido inicializado,
     *         o que normalmente indica um erro no ciclo de vida do teste.
     */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "Nenhum driver inicializado para esta thread. "
                            + "Garante que o @BeforeMethod do BaseTest correu.");
        }
        return driver;
    }

    /**
     * Encerra a sessao e limpa o ThreadLocal.
     *
     * <p>Remover o valor do ThreadLocal e <b>essencial</b>: as threads do pool
     * do TestNG sao reutilizadas, e deixar um driver "morto" agarrado causaria
     * fugas de memoria e sessoes fantasma.</p>
     */
    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Erro ao encerrar o driver (ignorado): {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }
}
