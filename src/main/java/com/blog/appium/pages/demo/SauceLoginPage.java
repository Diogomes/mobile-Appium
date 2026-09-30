package com.blog.appium.pages.demo;

import com.blog.appium.pages.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object do ecra de login da app demo da Sauce Labs
 * ({@code Android.SauceLabs.Mobile.Sample.app}).
 *
 * <p>Esta app publica e usada em tutoriais de Appium e expoe
 * {@code accessibility id} estaveis (prefixo {@code test-}). E o alvo usado
 * para gerar o video de demonstracao do framework.</p>
 *
 * <p>Credenciais validas: {@code standard_user} / {@code secret_sauce}.</p>
 */
public class SauceLoginPage extends BasePage {

    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(accessibility = "test-Username")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(accessibility = "test-Password")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(accessibility = "test-LOGIN")
    private WebElement loginButton;

    @Step("Login na app demo com '{username}'")
    public SauceProductsPage loginAs(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return new SauceProductsPage();
    }
}
