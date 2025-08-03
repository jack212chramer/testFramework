package core;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PayloadLoader {

    public static final String DATA_DIRECTORY = "src/test/resources/data";

    public static String getPayload(Endpoint endpoint) {
        return getPayload(endpoint.getName());
    }

    public static String getPayload(String filename) {
        return loadPayloadFromFile(filename);
    }

    private static String loadPayloadFromFile(String filename) {
        File jsonPath = new File(DATA_DIRECTORY + "/payloads/" + filename + ".json");
        try {
            if (jsonPath.exists()) {
                return new String(Files.readAllBytes(Paths.get(jsonPath.toString())));
            } else {
                throw new IllegalStateException("File not found");
            }
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    public static String loadSchema(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(DATA_DIRECTORY + "/schemas/" + filename + ".json")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}