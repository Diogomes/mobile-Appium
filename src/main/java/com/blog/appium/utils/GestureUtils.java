package com.blog.appium.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * Gestos mobile (swipe, scroll, tap) implementados com a <b>W3C Actions API</b>.
 *
 * <p>As antigas APIs {@code TouchAction}/{@code MultiTouchAction} foram
 * descontinuadas no Appium Java Client. A abordagem atual e usar
 * {@link PointerInput} e {@link Sequence}, que e o padrao W3C suportado tanto
 * em Android como em iOS — dai viver num utilitario partilhado.</p>
 */
public final class GestureUtils {

    private GestureUtils() {
    }

    /**
     * Swipe vertical de baixo para cima (scroll para baixo no conteudo),
     * usando percentagens do ecra para ser independente da resolucao.
     */
    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.80);
        int endY = (int) (size.height * 0.20);
        performVerticalSwipe(driver, startX, startY, endY);
    }

    /** Swipe vertical de cima para baixo. */
    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.20);
        int endY = (int) (size.height * 0.80);
        performVerticalSwipe(driver, startX, startY, endY);
    }

    private static void performVerticalSwipe(AppiumDriver driver, int x, int startY, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(200)))
                .addAction(finger.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }

    /** Tap nas coordenadas do centro de um elemento. */
    public static void tap(AppiumDriver driver, WebElement element) {
        int x = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int y = element.getLocation().getY() + element.getSize().getHeight() / 2;
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(100)))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }
}
