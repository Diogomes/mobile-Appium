package com.blog.appium.tests;

import com.blog.appium.base.BaseTest;
import com.blog.appium.data.User;
import com.blog.appium.pages.HomePage;
import com.blog.appium.pages.LoginPage;
import com.blog.appium.providers.DataProviders;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Login data-driven: um unico metodo de teste, varios cenarios alimentados
 * pelo JSON {@code testdata/users.json}.
 *
 * <p>Cada utilizador no ficheiro gera uma execucao independente. Adicionar um
 * novo caso (ex: conta expirada) e so acrescentar uma entrada no JSON — zero
 * codigo novo. E o grande valor do data-driven testing.</p>
 */
@Epic("Autenticacao")
@Feature("Login data-driven")
public class DataDrivenLoginTest extends BaseTest {

    @Test(dataProvider = "users", dataProviderClass = DataProviders.class,
          description = "Valida multiplos cenarios de login a partir de dados externos")
    @Description("Percorre todos os utilizadores do JSON e valida sucesso ou erro esperado.")
    public void login(User user) {
        LoginPage loginPage = new LoginPage();

        if (user.shouldSucceed()) {
            HomePage home = loginPage.loginAs(user.username(), user.password());
            Assert.assertTrue(home.isLoaded(),
                    "Login deveria ter sucesso para: " + user.username());
        } else {
            loginPage.loginExpectingFailure(user.username(), user.password());
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Deveria mostrar erro para: " + user.username());
            Assert.assertEquals(loginPage.getErrorMessage(), user.expectedError(),
                    "Mensagem de erro inesperada para: " + user.username());
        }
    }
}
