package com.blog.appium.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object do ecra de login.
 *
 * <p>Repara como cada elemento declara <b>dois locators</b>: um para Android
 * ({@code @AndroidFindBy}) e outro para iOS ({@code @iOSXCUITFindBy}). Em tempo
 * de execucao, o PageFactory escolhe o correto conforme a plataforma — e por
 * isso que um unico Page Object serve as duas plataformas.</p>
 *
 * <p><b>Boa pratica de locators:</b> preferir {@code accessibility id}
 * (accessibilityIdentifier no iOS / content-desc no Android), porque e estavel,
 * rapido e funciona igual nas duas plataformas. Evitar XPath sempre que
 * possivel — e fragil e lento.</p>
 */
public class LoginPage extends BasePage {

    @AndroidFindBy(accessibility = "username_field")
    @iOSXCUITFindBy(accessibility = "username_field")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "password_field")
    @iOSXCUITFindBy(accessibility = "password_field")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "login_button")
    @iOSXCUITFindBy(accessibility = "login_button")
    private WebElement loginButton;

    @AndroidFindBy(accessibility = "login_error")
    @iOSXCUITFindBy(accessibility = "login_error")
    private WebElement errorMessage;

    @Step("Fazer login com utilizador '{username}'")
    public HomePage loginAs(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return new HomePage();
    }

    @Step("Submeter credenciais invalidas")
    public LoginPage loginExpectingFailure(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return this;
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return readText(errorMessage);
    }
}
