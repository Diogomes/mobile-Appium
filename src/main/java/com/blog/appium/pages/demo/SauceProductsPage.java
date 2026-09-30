package com.blog.appium.pages.demo;

import com.blog.appium.pages.BasePage;
import com.blog.appium.utils.GestureUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Catalogo de produtos da app demo Sauce Labs.
 */
public class SauceProductsPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='PRODUCTS']")
    @iOSXCUITFindBy(accessibility = "test-PRODUCTS")
    private WebElement screenTitle;

    @AndroidFindBy(accessibility = "test-Cart")
    @iOSXCUITFindBy(accessibility = "test-Cart")
    private WebElement cartButton;

    @Step("Confirmar que o catalogo carregou")
    public boolean isLoaded() {
        return isDisplayed(screenTitle);
    }

    @Step("Fazer scroll pelo catalogo")
    public SauceProductsPage scroll() {
        GestureUtils.swipeUp(driver);
        return this;
    }

    @Step("Adicionar o primeiro produto ao carrinho")
    public SauceProductsPage addFirstItemToCart() {
        List<WebElement> addButtons = driver.findElements(
                AppiumBy.accessibilityId("test-ADD TO CART"));
        if (addButtons.isEmpty()) {
            throw new IllegalStateException("Nenhum botao 'ADD TO CART' encontrado no catalogo");
        }
        click(addButtons.get(0));
        return this;
    }

    @Step("Abrir o carrinho")
    public SauceCartPage openCart() {
        click(cartButton);
        return new SauceCartPage();
    }
}
