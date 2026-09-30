package com.blog.appium.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Carrinho de compras. Demonstra <b>coletar varios elementos</b> (lista de itens)
 * em vez de um unico, usando {@code findElements}.
 */
public class CartPage extends BasePage {

    @AndroidFindBy(accessibility = "cart_title")
    @iOSXCUITFindBy(accessibility = "cart_title")
    private WebElement screenTitle;

    @AndroidFindBy(accessibility = "checkout_button")
    @iOSXCUITFindBy(accessibility = "checkout_button")
    private WebElement checkoutButton;

    @AndroidFindBy(accessibility = "empty_cart_message")
    @iOSXCUITFindBy(accessibility = "empty_cart_message")
    private WebElement emptyMessage;

    @Step("Confirmar que o carrinho foi carregado")
    public boolean isLoaded() {
        return isDisplayed(screenTitle);
    }

    /**
     * Conta os itens listados no carrinho.
     *
     * <p>Convencao: cada linha do carrinho tem {@code accessibility id =
     * "cart_item_row"}. {@code findElements} devolve uma lista (vazia se nenhum),
     * ideal para contar ou iterar sem rebentar quando nao ha resultados.</p>
     */
    public int getItemCount() {
        List<WebElement> items = driver.findElements(AppiumBy.accessibilityId("cart_item_row"));
        return items.size();
    }

    public boolean isEmpty() {
        return isDisplayed(emptyMessage);
    }

    @Step("Avancar para o checkout")
    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutPage();
    }
}
