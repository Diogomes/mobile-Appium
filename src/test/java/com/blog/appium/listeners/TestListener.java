package com.blog.appium.listeners;

import com.blog.appium.driver.DriverManager;
import com.blog.appium.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Listener do TestNG que reage aos eventos do ciclo de vida dos testes.
 *
 * <p>O grande valor esta no {@link #onTestFailure}: captura automaticamente um
 * screenshot no momento exato da falha e anexa-o ao relatorio Allure. Assim,
 * nenhum teste precisa de se preocupar com evidencias — e tratado de forma
 * transversal (cross-cutting), uma boa pratica de separacao de
 * responsabilidades.</p>
 *
 * <p>Registado globalmente via {@code <listeners>} nas suites TestNG.</p>
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> INICIO: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<<< SUCESSO: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("<<< FALHA: {} | causa: {}", testName,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "desconhecida");
        try {
            ScreenshotUtils.attachToAllure(DriverManager.getDriver(), "Falha - " + testName);
        } catch (Exception e) {
            log.warn("Nao foi possivel capturar screenshot da falha: {}", e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<<< IGNORADO: {}", result.getMethod().getMethodName());
    }
}
