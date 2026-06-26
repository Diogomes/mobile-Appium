package com.blog.appium.tests;

import com.blog.appium.base.BaseTest;
import com.blog.appium.listeners.RetryAnalyzer;
import com.blog.appium.pages.HomePage;
import com.blog.appium.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Testes do fluxo de autenticacao.
 *
 * <p>Repara que os testes NAO contem locators nem chamadas ao driver — falam
 * apenas a "linguagem do utilizador" atraves dos Page Objects. Isto e o
 * objetivo do POM: testes legiveis e resistentes a mudancas de UI.</p>
 *
 * <p>As anotacoes Allure ({@code @Epic}, {@code @Feature}, {@code @Severity})
 * enriquecem o relatorio com contexto de negocio.</p>
 */
@Epic("Autenticacao")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(description = "Login com credenciais validas leva ao ecra inicial",
          retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica o caminho feliz: utilizador valido autentica-se e ve o ecra inicial.")
    public void loginComSucesso() {
        HomePage home = new LoginPage().loginAs("utilizador.valido", "Password123");

        Assert.assertTrue(home.isLoaded(),
                "O ecra inicial deveria estar visivel apos um login com sucesso");
    }

    @Test(description = "Login com credenciais invalidas mostra mensagem de erro")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifica que credenciais erradas nao autenticam e mostram erro ao utilizador.")
    public void loginComCredenciaisInvalidas() {
        LoginPage login = new LoginPage()
                .loginExpectingFailure("utilizador.invalido", "senha.errada");

        Assert.assertTrue(login.isErrorDisplayed(),
                "Deveria ser apresentada uma mensagem de erro");
    }
}
