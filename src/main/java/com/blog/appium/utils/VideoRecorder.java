package com.blog.appium.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Grava o ecra durante a execucao de um teste e produz um ficheiro {@code .mp4}.
 *
 * <h2>Como funciona</h2>
 * <p>Tanto o {@code AndroidDriver} como o {@code IOSDriver} implementam
 * {@link CanRecordScreen}. {@code startRecordingScreen()} inicia a captura no
 * proprio dispositivo; {@code stopRecordingScreen()} devolve o video em Base64.
 * Aqui descodificamos esse Base64, gravamos em {@code recordings/} e anexamos o
 * video ao relatorio Allure.</p>
 *
 * <h2>Boa pratica</h2>
 * <p>A gravacao e <b>opcional e configuravel</b> ({@code video.enabled}) e, por
 * omissao, so o video de testes que <b>falham</b> e guardado
 * ({@code video.onlyOnFailure=true}). Vídeo de todos os testes incha o disco e o
 * relatorio; o valor de diagnostico esta sobretudo nas falhas.</p>
 */
public final class VideoRecorder {

    private static final Logger log = LogManager.getLogger(VideoRecorder.class);
    private static final Path OUTPUT_DIR = Path.of("recordings");

    private VideoRecorder() {
    }

    /** Inicia a gravacao de ecra na sessao atual (silencioso em caso de erro). */
    public static void start(AppiumDriver driver) {
        try {
            ((CanRecordScreen) driver).startRecordingScreen();
        } catch (Exception e) {
            log.warn("Nao foi possivel iniciar a gravacao de video: {}", e.getMessage());
        }
    }

    /**
     * Para a gravacao e, se {@code keep} for true, grava o {@code .mp4} em disco
     * e anexa-o ao Allure. Se {@code keep} for false, descarta o video.
     *
     * @param driver   sessao atual
     * @param testName nome do teste (usado no nome do ficheiro/anexo)
     * @param keep     se o video deve ser guardado (ex: o teste falhou)
     */
    public static void stop(AppiumDriver driver, String testName, boolean keep) {
        String base64;
        try {
            base64 = ((CanRecordScreen) driver).stopRecordingScreen();
        } catch (Exception e) {
            log.warn("Nao foi possivel parar a gravacao de video: {}", e.getMessage());
            return;
        }
        if (!keep || base64 == null || base64.isBlank()) {
            return;
        }
        try {
            byte[] video = Base64.getDecoder().decode(base64);
            Files.createDirectories(OUTPUT_DIR);
            Path file = OUTPUT_DIR.resolve(testName + ".mp4");
            Files.write(file, video);
            Allure.addAttachment(testName + " (video)", "video/mp4",
                    new ByteArrayInputStream(video), "mp4");
            log.info("Video do teste guardado em: {}", file.toAbsolutePath());
        } catch (Exception e) {
            log.warn("Falha ao guardar/anexar o video: {}", e.getMessage());
        }
    }
}
