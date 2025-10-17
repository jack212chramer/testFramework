package core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PayloadLoader {

    public static final String DATA_DIRECTORY = "data";

    public static String getPayload(Endpoint endpoint) {
        return getPayload(endpoint.getName());
    }

    public static String getPayload(String filename) {
        return loadPayloadFromFile(filename);
    }

    private static String loadPayloadFromFile(String filename) {
        String resourcePath = DATA_DIRECTORY + "/payloads/" + filename + ".json";
        try (InputStream inputStream = openResource(resourcePath)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new UncheckedIOException("Unable to read payload: " + resourcePath, ioException);
        }
    }

    public static String loadSchema(String filename) {
        String resourcePath = DATA_DIRECTORY + "/schemas/" + filename + ".json";
        try (InputStream inputStream = openResource(resourcePath)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new UncheckedIOException("Unable to read schema: " + resourcePath, ioException);
        }
    }

    private static InputStream openResource(String resourcePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }
        return inputStream;
    }
}