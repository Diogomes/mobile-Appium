package com.blog.appium.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Modelo de dados de um utilizador de teste.
 *
 * <p>Usar um <b>record</b> (em vez de Object[] com indices) torna os dados
 * fortemente tipados e auto-documentados: no teste lemos {@code user.username()}
 * em vez de adivinhar o que e {@code data[0]}. O Jackson desserializa records
 * diretamente a partir do JSON.</p>
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} torna o parsing
 * resiliente: campos extra no JSON nao quebram a leitura.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        String username,
        String password,
        boolean shouldSucceed,
        String expectedError) {
}
