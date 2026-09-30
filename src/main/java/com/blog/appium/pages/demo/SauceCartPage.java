package com.blog.appium.pages.demo;

import com.blog.appium.pages.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Carrinho da app demo Sauce Labs.
 */
public class SauceCartPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='YOUR CART']")
    @iOSXCUITFindBy(accessibility = "test-Cart Content")
    private WebElement screenTitle;

    @Step("Confirmar que o carrinho carregou")
    public boolean isLoaded() {
        return isDisplayed(screenTitle);
    }

    @Step("Contar itens no carrinho")
    public int getItemCount() {
        List<WebElement> items = driver.findElements(
                AppiumBy.accessibilityId("test-Item Title"));
        return items.size();
    }
}
