package com.blog.appium.providers;

import com.blog.appium.data.TestDataReader;
import com.blog.appium.data.User;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * Fontes de dados para testes data-driven do TestNG.
 *
 * <p>Concentrar os {@code @DataProvider} numa classe dedicada mantem os testes
 * limpos e permite reutilizar a mesma fonte de dados em varios testes. Cada
 * linha devolvida gera uma execucao independente do {@code @Test}.</p>
 */
public class DataProviders {

    @DataProvider(name = "users")
    public static Object[][] users() {
        List<User> users = TestDataReader.readList("testdata/users.json", User.class);
        return users.stream()
                .map(user -> new Object[]{user})
                .toArray(Object[][]::new);
    }
}
