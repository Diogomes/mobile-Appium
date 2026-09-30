package com.blog.appium.pages;

import com.blog.appium.utils.GestureUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Catalogo de produtos. Demonstra dois cenarios complexos:
 * <ul>
 *   <li><b>Localizacao dinamica</b>: o item depende de dados (nome do produto),
 *       por isso o locator e construido em runtime, nao anotado num campo fixo.</li>
 *   <li><b>Scroll ate ao elemento</b>: listas longas exigem swipe ate o item
 *       entrar no ecra.</li>
 * </ul>
 */
public class ProductsPage extends BasePage {

    @AndroidFindBy(accessibility = "products_title")
    @iOSXCUITFindBy(accessibility = "products_title")
    private WebElement screenTitle;

    @AndroidFindBy(accessibility = "search_field")
    @iOSXCUITFindBy(accessibility = "search_field")
    private WebElement searchField;

    @AndroidFindBy(accessibility = "cart_badge")
    @iOSXCUITFindBy(accessibility = "cart_badge")
    private WebElement cartBadge;

    @AndroidFindBy(accessibility = "open_cart")
    @iOSXCUITFindBy(accessibility = "open_cart")
    private WebElement openCartButton;

    private static final int MAX_SWIPES = 6;

    @Step("Confirmar que o catalogo foi carregado")
    public boolean isLoaded() {
        return isDisplayed(screenTitle);
    }

    @Step("Pesquisar por '{term}'")
    public ProductsPage search(String term) {
        type(searchField, term);
        return this;
    }

    /**
     * Localiza um produto pelo nome, faz scroll ate ele aparecer e abre o detalhe.
     *
     * <p>O locator e construido em runtime porque o nome do produto e um dado
     * de teste, nao um valor fixo. Usamos {@code accessibility id} convencionado
     * como {@code product_item_<nome>} — combinar de forma deterministica o id
     * com o dado evita XPaths frageis.</p>
     */
    @Step("Abrir o produto '{productName}'")
    public ProductDetailsPage openProduct(String productName) {
        String accessibilityId = "product_item_" + productName;
        WebElement product = driver.findElement(AppiumBy.accessibilityId(accessibilityId));

        boolean visible = GestureUtils.swipeUpUntilVisible(driver, product, MAX_SWIPES);
        if (!visible) {
            throw new IllegalStateException(
                    "Produto nao encontrado no catalogo apos scroll: " + productName);
        }
        click(product);
        return new ProductDetailsPage();
    }

    @Step("Abrir o carrinho")
    public CartPage openCart() {
        click(openCartButton);
        return new CartPage();
    }

    /** Le o numero de itens no badge do carrinho (0 se ausente). */
    public int getCartCount() {
        if (!isDisplayed(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(readText(cartBadge).trim());
    }
}
