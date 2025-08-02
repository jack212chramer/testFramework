package core;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

@Getter
@Slf4j
public class ApiHandler {

    PayloadLoader payloadLoader = new PayloadLoader();
    private Endpoint endpoint;
    private Response response;
    private List<Header> headers = getDefaultHeaders();
    private final List<String> apiCalls = new ArrayList<>();
    private final Map<String, String> rememberedParams = new HashMap<>();

    protected ApiHandler() {
        RestAssured.baseURI = "https://fakerestapi.azurewebsites.net";
    }

    public ApiHandler setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    private String getEndpointUrl() {
        if (Objects.isNull(endpoint)) throw new IllegalStateException("Endpoint is not set");
        return replaceRememberedParams(endpoint.getUrl(), true);
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
        body = replaceRememberedParams(body, false);
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
        body = replaceRememberedParams(body, false);
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

    public Response get(Map<String, String> params) {
        replaceRememberedParams(params);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nGET request: %s\nHeaders:\n%s\nParams:\n%s\n",
                url, headers, params
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.GET,
                given().headers(new Headers(headers))
                        .params(params)
                        .accept(ContentType.JSON)
        );
    }

    public Response delete(Map<String, String> params) {
        replaceRememberedParams(params);
        String url = RestAssured.baseURI + getEndpointUrl();
        String reqLog = String.format(
                "\nDELETE request: %s\nHeaders:\n%s\nParams:\n%s\n",
                url, headers, params
        );
        log.info(reqLog);
        apiCalls.add(reqLog);

        return execute(Method.DELETE,
                given().headers(new Headers(headers))
                        .params(params)
        );
    }

    public List<Header> getDefaultHeaders() {
        return new ArrayList<>() {{
                add(new Header("Accept", "*/*"));
                add(new Header("Accept-Encoding", "gzip, deflate, br"));
                add(new Header("Connection", "keep-alive"));
            }};
    }

    public ApiHandler rememberValue(String key, String value) {
        log.info("Remembering value: {} = {}", key, value);
        rememberedParams.put(key, value);
        return this;
    }

    // ${parameter_name} or ${parameter_name::default_value}
    private final Pattern pattern = Pattern.compile("\\$\\{([^}]+?)(?:::([^}]+))?}");

    public Map<String, String> replaceRememberedParams(Map<String, String> input) {
        for (Map.Entry<String, String> entry : input.entrySet()) {
            StringBuilder output = new StringBuilder();
            Matcher matcher = pattern.matcher(entry.getValue());
            while (matcher.find()) {
                String key = matcher.group(1);
                String replacement = rememberedParams.get(key);
                String defaultValue = matcher.group(2);
                if (replacement != null) {
                    matcher.appendReplacement(output, replacement);
                } else if (defaultValue != null) {
                    matcher.appendReplacement(output, defaultValue);
                } else {
                    throw new IllegalStateException("No remembered value for key: " + key);
                }
            }
            matcher.appendTail(output);
            input.put(entry.getKey(), output.toString());
        }
        return input;
    }

    public String replaceRememberedParams(String input, boolean urlEncode) {
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

    public ApiHandler setParams(Map<String, String> params) {
        params = new HashMap<>(params);
        //replace all null value with empty string
        params.replaceAll((k, v) -> Objects.isNull(v) ? StringUtils.EMPTY : v);
        rememberedParams.putAll(params);
        return this;
    }
}