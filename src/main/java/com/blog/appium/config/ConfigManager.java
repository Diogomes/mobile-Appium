package com.blog.appium.config;

import com.blog.appium.driver.Platform;

import java.io.InputStream;
import java.util.Properties;

/**
 * Carrega e expoe a configuracao do framework a partir dos ficheiros
 * .properties em {@code src/test/resources/config}.
 *
 * <h2>Ordem de precedencia (do mais forte para o mais fraco)</h2>
 * <ol>
 *   <li>Propriedades de sistema (-Dchave=valor na linha de comando / CI)</li>
 *   <li>Ficheiro especifico da plataforma (android.properties / ios.properties)</li>
 *   <li>Ficheiro comum (config.properties)</li>
 * </ol>
 *
 * <p>Esta ordem e uma boa pratica: o valor base fica versionado no repositorio,
 * mas qualquer ambiente (CI, maquina local, cloud) consegue sobrepor sem editar
 * ficheiros — basta passar -Dchave=valor.</p>
 *
 * <p>A classe e <b>imutavel apos construcao</b> e nao tem estado partilhado
 * mutavel, sendo segura para uso em paralelo.</p>
 */
public final class ConfigManager {

    private final Properties properties = new Properties();
    private final Platform platform;

    private ConfigManager(Platform platform) {
        this.platform = platform;
        loadCommonConfig();
        loadPlatformConfig(platform);
    }

    /**
     * Cria o gestor de configuracao para a plataforma indicada.
     * A plataforma e tipicamente resolvida a partir de -Dplatform.
     */
    public static ConfigManager forPlatform(Platform platform) {
        return new ConfigManager(platform);
    }

    private void loadCommonConfig() {
        load("config/config.properties");
    }

    private void loadPlatformConfig(Platform platform) {
        String file = platform == Platform.ANDROID
                ? "config/android.properties"
                : "config/ios.properties";
        load(file);
    }

    private void load(String resourcePath) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Ficheiro de configuracao nao encontrado: " + resourcePath);
            }
            properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao carregar configuracao: " + resourcePath, e);
        }
    }

    /**
     * Devolve uma propriedade obrigatoria. Falha cedo (fail-fast) se nao existir,
     * em vez de propagar um null que so causaria erro mais tarde.
     */
    public String get(String key) {
        // -D na linha de comando tem prioridade maxima
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriedade obrigatoria em falta: '" + key + "'");
        }
        return value.trim();
    }

    /** Devolve uma propriedade opcional, com valor por omissao. */
    public String get(String key, String defaultValue) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        return properties.getProperty(key, defaultValue).trim();
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    public Platform getPlatform() {
        return platform;
    }
}
