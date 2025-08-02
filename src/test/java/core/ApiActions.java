package core;

import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public class ApiActions {

    private static ApiActions instance;
    public static ApiActions getInstance() {
        if (instance == null) {
            instance = new ApiActions();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private ApiActions() {
        // Private constructor to enforce singleton pattern
    }

    private final ApiHandler apiHandler = new ApiHandler();

    public void setParams(Map<String, String> unmodifiableParams) {
        apiHandler.setParams(new HashMap<>(unmodifiableParams));
    }

    public void rememberResponseValue(String jsonPath) {
        ensureResponse();
        Object raw = JsonPath.parse(apiHandler.getResponse().asString()).read(jsonPath);
        apiHandler.rememberValue(jsonPath, String.valueOf(raw));
    }

    public void rememberResponseValueWithName(String jsonPath, String name) {
        ensureResponse();
        Object raw = JsonPath.parse(apiHandler.getResponse().asString()).read(jsonPath);
        apiHandler.rememberValue(name, String.valueOf(raw));
    }

    private void ensureResponse() {
        if (apiHandler.getResponse() == null) {
            throw new IllegalStateException("No response available. Send a request first.");
        }
    }

    public void sendPostRequest(Endpoint endpoint) {
        String body = apiHandler.getPayloadLoader().getPayload(endpoint);
        apiHandler.setEndpoint(endpoint)
                .post(body);
    }

    public void sendPostRequestWithBodyName(Endpoint endpoint, String bodyFile) {
        apiHandler.setEndpoint(endpoint);
        String body = apiHandler.getPayloadLoader().getPayload(bodyFile);
        apiHandler.post(body);
    }

    public void sendGetRequest(Endpoint endpoint) {
        apiHandler.setEndpoint(endpoint)
                .get(Map.of());
    }

    public void sendGetRequestWithParams(Endpoint endpoint, Map<String, String> unmodifiableMap) {
        apiHandler.setEndpoint(endpoint)
                .get(new HashMap<>(unmodifiableMap));
    }

    public void sendDeleteRequest(Endpoint endpoint) {
        apiHandler.setEndpoint(endpoint)
                .delete(Map.of());
    }

    public void sendPutRequestWithBodyName(Endpoint endpoint, String bodyFile) {
        apiHandler.setEndpoint(endpoint);
        String body = apiHandler.getPayloadLoader().getPayload(bodyFile);
        apiHandler.put(body);
    }

    public Endpoint getEndpoint(String endpoint) {
        return PayloadLoader.getEndpointByName(endpoint);
    }
}
