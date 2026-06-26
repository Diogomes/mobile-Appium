package com.blog.appium.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Esperas explicitas reutilizaveis.
 *
 * <h2>Boa pratica central em automacao mobile</h2>
 * <p>Nunca usar {@code Thread.sleep()}. Apps mobile tem tempos de resposta
 * variaveis (animacoes, rede, render). Esperas explicitas aguardam por uma
 * <i>condicao</i> (elemento visivel, clicavel) ate um limite, tornando os
 * testes simultaneamente <b>mais rapidos</b> (avancam assim que a condicao
 * e satisfeita) e <b>mais estaveis</b> (nao falham por timing fixo).</p>
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    public static WebElement waitForVisibility(AppiumDriver driver, WebElement element, int timeoutSeconds) {
        return newWait(driver, timeoutSeconds)
                .until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickable(AppiumDriver driver, WebElement element, int timeoutSeconds) {
        return newWait(driver, timeoutSeconds)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForInvisibility(AppiumDriver driver, WebElement element, int timeoutSeconds) {
        return newWait(driver, timeoutSeconds)
                .until(ExpectedConditions.invisibilityOf(element));
    }

    private static WebDriverWait newWait(AppiumDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
}
