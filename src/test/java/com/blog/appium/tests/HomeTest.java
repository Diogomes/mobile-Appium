package com.blog.appium.tests;

import com.blog.appium.base.BaseTest;
import com.blog.appium.pages.HomePage;
import com.blog.appium.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Testes do ecra inicial, partindo de um utilizador ja autenticado.
 */
@Epic("Autenticacao")
@Feature("Sessao")
public class HomeTest extends BaseTest {

    @Test(description = "Terminar sessao devolve o utilizador ao ecra de login")
    public void logoutVoltaAoLogin() {
        HomePage home = new LoginPage().loginAs("utilizador.valido", "Password123");
        Assert.assertTrue(home.isLoaded(), "Pre-condicao: ecra inicial carregado");

        LoginPage login = home.logout();

        Assert.assertNotNull(login, "Apos logout deveriamos voltar ao ecra de login");
    }
}
