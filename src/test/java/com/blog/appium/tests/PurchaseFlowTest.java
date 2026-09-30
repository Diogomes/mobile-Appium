package com.blog.appium.tests;

import com.blog.appium.base.BaseTest;
import com.blog.appium.listeners.RetryAnalyzer;
import com.blog.appium.pages.CartPage;
import com.blog.appium.pages.CheckoutPage;
import com.blog.appium.pages.HomePage;
import com.blog.appium.pages.LoginPage;
import com.blog.appium.pages.ProductDetailsPage;
import com.blog.appium.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cenario complexo ponta-a-ponta: login → catalogo → detalhe → carrinho →
 * checkout → confirmacao.
 *
 * <p>Mostra como Page Objects encadeados produzem um teste que se le como uma
 * jornada de utilizador, apesar de cobrir 6 ecras e gestos de scroll. Toda a
 * complexidade tecnica (esperas, swipes, locators dinamicos) esta escondida nos
 * Page Objects — o teste so descreve intencao e verifica resultados.</p>
 */
@Epic("Compras")
@Feature("Fluxo de compra")
public class PurchaseFlowTest extends BaseTest {

    private static final String PRODUTO = "tenis_running";

    @Test(description = "Compra completa de um produto ate a confirmacao da encomenda",
          retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Caminho critico de negocio: um utilizador autenticado compra um produto com sucesso.")
    public void compraCompletaComSucesso() {
        // 1. Autenticacao
        HomePage home = new LoginPage().loginAs("utilizador.valido", "Password123");
        Assert.assertTrue(home.isLoaded(), "Pre-condicao: ecra inicial carregado");

        // 2. Catalogo: pesquisar e abrir o produto (com scroll automatico)
        ProductsPage products = home.openProducts();
        Assert.assertTrue(products.isLoaded(), "Catalogo deveria estar visivel");
        ProductDetailsPage details = products.search("tenis").openProduct(PRODUTO);

        // 3. Adicionar ao carrinho e voltar
        String nomeProduto = details.getProductName();
        products = details.addToCart().goBack();
        Assert.assertEquals(products.getCartCount(), 1,
                "O badge do carrinho deveria indicar 1 item");

        // 4. Carrinho
        CartPage cart = products.openCart();
        Assert.assertEquals(cart.getItemCount(), 1,
                "O carrinho deveria conter exatamente 1 item (" + nomeProduto + ")");

        // 5. Checkout com preenchimento de formulario
        CheckoutPage checkout = cart.proceedToCheckout()
                .fillShippingDetails("Diogo Gomes", "Rua das Flores, 123", "4000-001")
                .confirmOrder();

        // 6. Verificacao final
        Assert.assertTrue(checkout.isOrderConfirmed(),
                "A encomenda deveria estar confirmada no final do fluxo");
    }

    @Test(description = "Carrinho inicia vazio para um novo utilizador")
    @Severity(SeverityLevel.NORMAL)
    public void carrinhoIniciaVazio() {
        HomePage home = new LoginPage().loginAs("utilizador.valido", "Password123");
        CartPage cart = home.openProducts().openCart();

        Assert.assertTrue(cart.isEmpty(), "Um carrinho novo deveria estar vazio");
    }
}
