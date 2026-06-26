package com.blog.appium.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Repete automaticamente um teste que falhou, ate um numero maximo de tentativas.
 *
 * <h2>Quando usar (e quando NAO usar)</h2>
 * <p>Testes mobile podem falhar por motivos transitorios (rede lenta, emulador
 * a "aquecer", ANRs ocasionais). Um retry controlado reduz o ruido desses
 * falsos negativos. <b>Atencao:</b> retry nao deve mascarar bugs reais nem
 * testes genuinamente instaveis — a instabilidade cronica deve ser corrigida,
 * nao escondida com retries.</p>
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    // Le o limite de -DmaxRetries, com 1 retry por omissao
    private static final int MAX_RETRIES = Integer.getInteger("maxRetries", 1);

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRIES) {
            attempt++;
            log.warn("A repetir '{}' (tentativa {}/{})",
                    result.getMethod().getMethodName(), attempt, MAX_RETRIES);
            return true;
        }
        return false;
    }
}
