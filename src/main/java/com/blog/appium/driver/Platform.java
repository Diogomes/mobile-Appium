package com.blog.appium.driver;

/**
 * Plataformas mobile suportadas pelo framework.
 *
 * <p>Centralizar as plataformas num enum evita "strings magicas" espalhadas
 * pelo codigo (ex: if ("android".equals(...))) e da seguranca em tempo de
 * compilacao quando escolhemos o driver ou os locators corretos.</p>
 */
public enum Platform {

    ANDROID,
    IOS;

    /**
     * Converte uma string (vinda de -Dplatform ou de um properties) no enum,
     * de forma tolerante a maiusculas/minusculas e espacos.
     *
     * @param value valor textual, ex: "android", "iOS"
     * @return a plataforma correspondente
     * @throws IllegalArgumentException se a plataforma for desconhecida
     */
    public static Platform from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Plataforma nao definida. Use -Dplatform=android|ios");
        }
        try {
            return Platform.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Plataforma invalida: '" + value + "'. Valores aceites: android, ios");
        }
    }
}
