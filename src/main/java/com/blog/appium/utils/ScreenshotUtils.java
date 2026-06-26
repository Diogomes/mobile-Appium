package com.blog.appium.utils;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

/**
 * Captura de screenshots para evidencia de testes.
 *
 * <p>Anexar o screenshot diretamente ao relatorio Allure (em vez de so o gravar
 * em disco) torna o diagnostico de falhas muito mais rapido: quem analisa o
 * relatorio ve logo o estado do ecra no momento do erro.</p>
 */
public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /** Anexa um screenshot PNG ao relatorio Allure com o nome indicado. */
    public static void attachToAllure(AppiumDriver driver, String name) {
        try {
            byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            // Falha ao capturar nunca deve mascarar o erro real do teste
            Allure.addAttachment("Falha ao capturar screenshot", e.getMessage());
        }
    }
}
