package com.blog.appium.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

/**
 * Le ficheiros de dados de teste (JSON) a partir do classpath e converte-os
 * em objetos tipados.
 *
 * <h2>Porque separar dados do codigo?</h2>
 * <p>Manter credenciais, produtos e moradas em ficheiros JSON (e nao "hardcoded"
 * no teste) permite alterar/expandir cenarios <b>sem recompilar</b>, partilhar
 * os mesmos dados entre testes e alimentar testes data-driven com dezenas de
 * combinacoes. E o principio "data-driven testing".</p>
 */
public final class TestDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataReader() {
    }

    /**
     * Le uma lista de objetos de um JSON no classpath.
     *
     * @param resourcePath caminho relativo a resources, ex: "testdata/users.json"
     * @param type         classe dos elementos da lista
     */
    public static <T> List<T> readList(String resourcePath, Class<T> type) {
        try (InputStream in = TestDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Ficheiro de dados nao encontrado: " + resourcePath);
            }
            var listType = MAPPER.getTypeFactory().constructCollectionType(List.class, type);
            return MAPPER.readValue(in, listType);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao ler dados de teste: " + resourcePath, e);
        }
    }
}
