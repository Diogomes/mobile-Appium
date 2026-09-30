package com.blog.appium.pages;

import com.blog.appium.utils.GestureUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Checkout: preenche a morada e confirma a encomenda.
 *
 * <p>Demonstra preenchimento de formulario com varios campos e o uso de
 * scroll para alcancar o botao de confirmacao quando o teclado/formulario
 * empurra o conteudo para fora do ecra.</p>
 */
public class CheckoutPage extends BasePage {

    @AndroidFindBy(accessibility = "fullname_field")
    @iOSXCUITFindBy(accessibility = "fullname_field")
    private WebElement fullNameField;

    @AndroidFindBy(accessibility = "address_field")
    @iOSXCUITFindBy(accessibility = "address_field")
    private WebElement addressField;

    @AndroidFindBy(accessibility = "zipcode_field")
    @iOSXCUITFindBy(accessibility = "zipcode_field")
    private WebElement zipCodeField;

    @AndroidFindBy(accessibility = "confirm_order")
    @iOSXCUITFindBy(accessibility = "confirm_order")
    private WebElement confirmOrderButton;

    @AndroidFindBy(accessibility = "order_confirmation")
    @iOSXCUITFindBy(accessibility = "order_confirmation")
    private WebElement orderConfirmation;

    @Step("Preencher dados de entrega")
    public CheckoutPage fillShippingDetails(String fullName, String address, String zipCode) {
        type(fullNameField, fullName);
        type(addressField, address);
        type(zipCodeField, zipCode);
        return this;
    }

    @Step("Confirmar a encomenda")
    public CheckoutPage confirmOrder() {
        // O botao pode ficar abaixo da dobra apos preencher o formulario
        GestureUtils.swipeUpUntilVisible(driver, confirmOrderButton, 3);
        click(confirmOrderButton);
        return this;
    }

    @Step("Verificar confirmacao da encomenda")
    public boolean isOrderConfirmed() {
        return isDisplayed(orderConfirmation);
    }
}
