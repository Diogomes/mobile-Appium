package com.blog.appium.driver;

import com.blog.appium.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.Duration;

/**
 * Fabrica responsavel por construir um {@link AppiumDriver} corretamente
 * configurado para cada plataforma.
 *
 * <p>Aplica o padrao <b>Factory</b>: o teste nao sabe (nem precisa de saber)
 * como um AndroidDriver ou um IOSDriver e criado — apenas pede "um driver para
 * esta plataforma". Isto isola toda a complexidade de capabilities num so lugar
 * e torna trivial adicionar uma nova plataforma no futuro.</p>
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
        // Classe utilitaria — nao deve ser instanciada
    }

    /**
     * Cria o driver Appium de acordo com a plataforma definida na configuracao.
     *
     * @param config configuracao ja resolvida para a plataforma alvo
     * @return driver pronto a usar
     */
    public static AppiumDriver create(ConfigManager config) {
        Platform platform = config.getPlatform();
        URL serverUrl = appiumServerUrl(config);

        log.info("A criar driver Appium | plataforma={} | servidor={}", platform, serverUrl);

        return switch (platform) {
            case ANDROID -> createAndroidDriver(config, serverUrl);
            case IOS -> createIosDriver(config, serverUrl);
        };
    }

    private static AndroidDriver createAndroidDriver(ConfigManager config, URL serverUrl) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setDeviceName(config.get("device.name"))
                .setPlatformVersion(config.get("platform.version", ""))
                .setAppPackage(config.get("app.package", ""))
                .setAppActivity(config.get("app.activity", ""))
                .setNewCommandTimeout(Duration.ofSeconds(config.getInt("newCommandTimeout", 120)));

        // O caminho da app (.apk) so e definido quando nao usamos uma app ja instalada
        String appPath = config.get("app.path", "");
        if (!appPath.isBlank()) {
            options.setApp(appPath);
        }
        // Evita reinstalar a app a cada teste, acelerando a execucao local
        options.setNoReset(config.getBoolean("noReset", false));
        options.setFullReset(config.getBoolean("fullReset", false));

        return new AndroidDriver(serverUrl, options);
    }

    private static IOSDriver createIosDriver(ConfigManager config, URL serverUrl) {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setAutomationName("XCUITest")
                .setDeviceName(config.get("device.name"))
                .setPlatformVersion(config.get("platform.version", ""))
                .setBundleId(config.get("app.bundleId", ""))
                .setNewCommandTimeout(Duration.ofSeconds(config.getInt("newCommandTimeout", 120)));

        String appPath = config.get("app.path", "");
        if (!appPath.isBlank()) {
            options.setApp(appPath);
        }
        options.setNoReset(config.getBoolean("noReset", false));
        options.setFullReset(config.getBoolean("fullReset", false));

        return new IOSDriver(serverUrl, options);
    }

    private static URL appiumServerUrl(ConfigManager config) {
        String url = config.get("appium.server.url", "http://127.0.0.1:4723");
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new IllegalStateException("URL do servidor Appium invalido: " + url, e);
        }
    }
}
