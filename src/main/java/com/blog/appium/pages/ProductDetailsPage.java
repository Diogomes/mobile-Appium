package com.blog.appium.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Detalhe de um produto. Permite adicionar ao carrinho e voltar ao catalogo.
 */
public class ProductDetailsPage extends BasePage {

    @AndroidFindBy(accessibility = "product_name")
    @iOSXCUITFindBy(accessibility = "product_name")
    private WebElement productName;

    @AndroidFindBy(accessibility = "product_price")
    @iOSXCUITFindBy(accessibility = "product_price")
    private WebElement productPrice;

    @AndroidFindBy(accessibility = "add_to_cart")
    @iOSXCUITFindBy(accessibility = "add_to_cart")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "back_to_products")
    @iOSXCUITFindBy(accessibility = "back_to_products")
    private WebElement backButton;

    public String getProductName() {
        return readText(productName);
    }

    public String getProductPrice() {
        return readText(productPrice);
    }

    @Step("Adicionar o produto ao carrinho")
    public ProductDetailsPage addToCart() {
        click(addToCartButton);
        return this;
    }

    @Step("Voltar ao catalogo")
    public ProductsPage goBack() {
        click(backButton);
        return new ProductsPage();
    }
}
