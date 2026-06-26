package com.blog.appium.pages;

import com.blog.appium.driver.DriverManager;
import com.blog.appium.utils.WaitUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * Classe base de todos os Page Objects.
 *
 * <h2>Padrao Page Object Model (POM)</h2>
 * <p>Cada ecra da app e representado por uma classe. Os testes interagem com
 * <i>metodos de negocio</i> ("fazer login", "abrir carrinho") e nunca com
 * locators diretamente. Vantagens:</p>
 * <ul>
 *   <li><b>Manutencao</b>: se um botao muda, corrige-se num so sitio.</li>
 *   <li><b>Legibilidade</b>: o teste le-se como uma historia de utilizador.</li>
 *   <li><b>Reuso</b>: o mesmo ecra serve varios testes.</li>
 * </ul>
 *
 * <p>O {@link AppiumFieldDecorator} inicializa os campos anotados com
 * {@code @AndroidFindBy}/{@code @iOSXCUITFindBy} nas subclasses, permitindo
 * que o <b>mesmo Page Object</b> funcione em Android e iOS — escolhe o locator
 * certo consoante a plataforma da sessao.</p>
 */
public abstract class BasePage {

    protected final AppiumDriver driver;
    protected static final int DEFAULT_TIMEOUT = 15;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        // Tempo de espera implicito do PageFactory ao localizar elementos
        PageFactory.initElements(
                new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_TIMEOUT)), this);
    }

    /** Escreve texto num campo, garantindo que esta visivel primeiro. */
    protected void type(WebElement element, String text) {
        WaitUtils.waitForVisibility(driver, element, DEFAULT_TIMEOUT);
        element.clear();
        element.sendKeys(text);
    }

    /** Toca num elemento depois de garantir que esta clicavel. */
    protected void click(WebElement element) {
        WaitUtils.waitForClickable(driver, element, DEFAULT_TIMEOUT).click();
    }

    /** Le o texto de um elemento depois de garantir visibilidade. */
    protected String readText(WebElement element) {
        return WaitUtils.waitForVisibility(driver, element, DEFAULT_TIMEOUT).getText();
    }

    /** Verifica se um elemento esta presente e visivel sem lancar excecao. */
    protected boolean isDisplayed(WebElement element) {
        try {
            return WaitUtils.waitForVisibility(driver, element, DEFAULT_TIMEOUT).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
