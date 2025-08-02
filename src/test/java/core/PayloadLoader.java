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
    public static final String ENDPOINT_PACKAGE = "core.endpoints";

    public String getPayload(Endpoint endpoint) {
        return getPayload(endpoint.getName());
    }

    public String getPayload(String filename) {
        return loadPayloadFromFile(filename);
    }

    private String loadPayloadFromFile(String filename) {
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

    public static Endpoint getEndpointByName(String name) {
        for (Class<? extends Endpoint> endpointClass : getEndpointClasses(ENDPOINT_PACKAGE)) {
            for (Endpoint endpoint : endpointClass.getEnumConstants()) {
                if (name.equals(endpoint.getName())) {
                    return endpoint;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }

    public static List<Class<? extends Endpoint>> getEndpointClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toList());
    }

    private static Class<? extends Endpoint> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')))
                    .asSubclass(Endpoint.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}