package steps;

import core.ApiActions;
import io.cucumber.java.pl.Kiedy;
import io.cucumber.java.pl.Wtedy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class GenericSteps {

    private final ApiActions apiActions = ApiActions.getInstance();

    @Kiedy("^I set params$")
    public void setParams(Map<String, String> unmodifiableParams) {
        apiActions.setParams(unmodifiableParams);
    }

    @Wtedy("^remember response value (.+?)$")
    public void rememberResponseValue(String value) {
        apiActions.rememberResponseValue(value);
    }

    @Wtedy("^remember response value: (.+?), with name (.+?)$")
    public void rememberResponseValueWithName(String value, String name) {
        apiActions.rememberResponseValueWithName(value, name);
    }


    @Kiedy("^I send POST request to (.+?)$")
    public void sendPostRequest(String endpoint) {
        apiActions.sendPostRequest(apiActions.getEndpoint(endpoint));
    }


    @Kiedy("^I send POST request to: (.+?) with bodyfile (.+?)$")
    public void sendPostRequestWithBodyName(String endpoint, String bodyFile) {
        apiActions.sendPostRequestWithBodyName(apiActions.getEndpoint(endpoint), bodyFile);
    }

    @Kiedy("^I send GET request to (.+?)$")
    public void sendGetRequest(String endpoint) {
        apiActions.sendGetRequest(apiActions.getEndpoint(endpoint));
    }

    @Kiedy("^I send GET request with params to (.+?)$")
    public void sendGetRequestWithParams(String endpoint, Map<String, String> unmodifiableMap) {
        apiActions.sendGetRequestWithParams(apiActions.getEndpoint(endpoint), unmodifiableMap);
    }

    @Kiedy("^I send DELETE request to (.+?)$")
    public void sendDeleteRequest(String endpoint) {
        apiActions.sendDeleteRequest(apiActions.getEndpoint(endpoint));
    }


    @Kiedy("^I send PUT request to (.+?) with bodyfile (.+?)$")
    public void sendPutRequestWithBodyName(String endpoint, String bodyFile) {
        apiActions.sendPutRequestWithBodyName(apiActions.getEndpoint(endpoint), bodyFile);
    }
}