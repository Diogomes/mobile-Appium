package com.blog.appium.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object do ecra inicial, mostrado apos um login com sucesso.
 */
public class HomePage extends BasePage {

    @AndroidFindBy(accessibility = "welcome_message")
    @iOSXCUITFindBy(accessibility = "welcome_message")
    private WebElement welcomeMessage;

    @AndroidFindBy(accessibility = "logout_button")
    @iOSXCUITFindBy(accessibility = "logout_button")
    private WebElement logoutButton;

    @Step("Verificar se o ecra inicial foi carregado")
    public boolean isLoaded() {
        return isDisplayed(welcomeMessage);
    }

    @Step("Obter mensagem de boas-vindas")
    public String getWelcomeMessage() {
        return readText(welcomeMessage);
    }

    @Step("Terminar sessao")
    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage();
    }
}
