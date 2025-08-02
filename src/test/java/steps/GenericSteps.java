package steps;

import core.ApiActions;
import io.cucumber.java.en.When;
import io.cucumber.java.pl.Wtedy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GenericSteps {

    private final ApiActions apiActions = ApiActions.getInstance();

    @When("^I set params$")
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


    @When("^I send POST request to (.+?)$")
    public void sendPostRequest(String endpoint) {
        apiActions.sendPostRequest(apiActions.getEndpoint(endpoint));
    }


    @When("^I send POST request to: (.+?) with bodyfile (.+?)$")
    public void sendPostRequestWithBodyName(String endpoint, String bodyFile) {
        apiActions.sendPostRequestWithBodyName(apiActions.getEndpoint(endpoint), bodyFile);
    }

    @When("^I send GET request to (.+?)$")
    public void sendGetRequest(String endpoint) {
        apiActions.sendGetRequest(apiActions.getEndpoint(endpoint));
    }

    @When("^I send GET request with params to (.+?)$")
    public void sendGetRequestWithParams(String endpoint, Map<String, String> unmodifiableMap) {
        apiActions.sendGetRequestWithParams(apiActions.getEndpoint(endpoint), unmodifiableMap);
    }

    @When("^I send DELETE request to (.+?)$")
    public void sendDeleteRequest(String endpoint) {
        apiActions.sendDeleteRequest(apiActions.getEndpoint(endpoint));
    }


    @When("^I send PUT request to (.+?) with bodyfile (.+?)$")
    public void sendPutRequestWithBodyName(String endpoint, String bodyFile) {
        apiActions.sendPutRequestWithBodyName(apiActions.getEndpoint(endpoint), bodyFile);
    }

    @When("^I (will|will not) encode path parameters$")
    public void encodePathParams(String value) {
        boolean encode = "will".equals(value.trim());
        apiActions.getApiHandler().setEncodePathParams(encode);
    }
}