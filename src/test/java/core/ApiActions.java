package core;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

@Slf4j
public class ApiActions {

    private Endpoint endpoint;
    private Response response;
    private final List<Header> headers = getDefaultHeaders();
    private final List<String> apiCalls = new ArrayList<>();
    private final Map<String, String> rememberedParams = new HashMap<>();
    @Setter
    private boolean encodePathParams = true;

    protected ApiActions() {
        RestAssured.baseURI = "https://fakerestapi.azurewebsites.net";
    }

    public ApiActions setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    private String getEndpointUrl() {
        if (Objects.isNull(endpoint)) throw new IllegalStateException("Endpoint is not set");
        return replacePlaceholders(endpoint.getUrl(), encodePathParams);
    }

    private Response execute(Method method, RequestSpecification spec) {
        String url = RestAssured.baseURI + getEndpointUrl();
        Response res = spec.request(method, getEndpointUrl());

        String respLog = String.format(
                "\nResponse: %s\nStatus: %d\nHeaders:\n%s\nBody:\n%s\n",
                url,
                res.statusCode(),
                res.headers().asList(),
                res.asPrettyString()
        );
        log.info(respLog);
        apiCalls.add(respLog);
        this.response = res;
        return res;
    }

    public Response post(String body) {
        body = replacePlaceholders(body, false);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nPOST request: %s\nHeaders:\n%s\nBody:\n%s\n",
                url, headers, body
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.POST,
                given().contentType(ContentType.JSON)
                        .headers(new Headers(headers))
                        .body(body)
        );
    }

    public Response put(String body) {
        body = replacePlaceholders(body, false);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nPUT request: %s\nHeaders:\n%s\nBody:\n%s\n",
                url, headers, body
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.PUT,
                given().contentType(ContentType.JSON)
                        .headers(new Headers(headers))
                        .body(body)
        );
    }

    public Response get(Map<String, String> queryParameters) {
        Map<String, String> processedParams = Objects.isNull(queryParameters)
                ? Collections.emptyMap()
                : replacePlaceholders(queryParameters);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nGET request: %s\nHeaders:\n%s\nParams:\n%s\n",
                url, headers, processedParams
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.GET,
                given().headers(new Headers(headers))
                        .params(processedParams)
                        .accept(ContentType.JSON)
        );
    }

    public Response delete(Map<String, String> params) {
        Map<String, String> processedParams = Objects.isNull(params)
                ? Collections.emptyMap()
                : replacePlaceholders(params);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nDELETE request: %s\nHeaders:\n%s\nParams:\n%s\n",
                url, headers, processedParams
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.DELETE,
                given().headers(new Headers(headers))
                        .params(processedParams)
        );
    }

    public List<Header> getDefaultHeaders() {
        return new ArrayList<>() {{
                add(new Header("Accept", "*/*"));
                add(new Header("Accept-Encoding", "gzip, deflate, br"));
                add(new Header("Connection", "keep-alive"));
            }};
    }

    public ApiActions rememberValue(String key, String value) {
        log.info("Remembering value: {} = {}", key, value);
        rememberedParams.put(key, value);
        return this;
    }

    // ${parameter_name} or ${parameter_name::default_value}
    private final Pattern pattern = Pattern.compile("\\$\\{([^}]+?)(?:::([^}]+))?}");

    public Map<String, String> replacePlaceholders(Map<String, String> input) {
        if (Objects.isNull(input) || input.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> resolved = new LinkedHashMap<>(input.size());
        for (Map.Entry<String, String> entry : input.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                resolved.put(key, null);
                continue;
            }

            StringBuilder output = new StringBuilder();
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String placeholderKey = matcher.group(1);
                String replacement = rememberedParams.get(placeholderKey);
                String defaultValue = matcher.group(2);
                if (replacement != null) {
                    matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
                } else if (defaultValue != null) {
                    matcher.appendReplacement(output, Matcher.quoteReplacement(defaultValue));
                } else {
                    throw new IllegalStateException("No remembered value for key: " + placeholderKey);
                }
            }
            matcher.appendTail(output);
            resolved.put(key, output.toString());
        }
        return Collections.unmodifiableMap(resolved);
    }

    public String replacePlaceholders(String input, boolean urlEncode) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String defaultValue = matcher.group(2);
            String replacement = rememberedParams.getOrDefault(key, defaultValue);
            if (Objects.isNull(replacement)) {
                throw new IllegalStateException("No remembered value for key: " + key);
            }
            if (urlEncode) {
                replacement = URLEncoder.encode(replacement, StandardCharsets.UTF_8);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public ApiActions setParams(Map<String, String> params) {
        params = new HashMap<>(params);
        //replace all null value with empty string
        params.replaceAll((k, v) -> Objects.isNull(v) ? StringUtils.EMPTY : v);
        rememberedParams.putAll(params);
        return this;
    }

    public Response getResponse() {
        return response;
    }

    public List<String> getApiCalls() {
        return List.copyOf(apiCalls);
    }
}