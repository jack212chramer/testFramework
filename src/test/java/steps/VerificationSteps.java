package steps;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import core.ApiActions;
import core.ApiHandler;
import core.PayloadLoader;
import io.cucumber.java.pl.Wtedy;
import org.junit.jupiter.api.Assertions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class VerificationSteps {

    private final ApiHandler apiHandler = ApiActions.getInstance().getApiHandler();

    @Wtedy("^I receive response code (.+?)$")
    public void checkResponseCode(int code) {
        apiHandler.getResponse()
            .then()
            .statusCode(code);
    }

    @Wtedy("^response contains (.+?)$")
    public void responseContains(String value) {
        Assertions.assertTrue(apiHandler.getResponse()
            .body()
            .asString()
            .contains(value), "Response does not contain " + value);
    }

    @Wtedy("^response contains: (.+?) with value (.+?)$")
    public void responseContains(String value, String pattern) {
        pattern = apiHandler.replacePlaceholders(pattern, false);
        DocumentContext jsonContext = JsonPath.parse(apiHandler.getResponse().asString());
        String responseValue = jsonContext.read(value).toString();
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(responseValue);
        Assertions.assertFalse(responseValue.isEmpty(), value + " is empty");
        Assertions.assertTrue(matcher.matches(), "Response does not contain parameter " + value + " with value " + pattern);
    }

    @Wtedy("^response schema is correct$")
    public void responseSchemaIsCorrect() {
        apiHandler.getResponse().then().assertThat().body(matchesJsonSchema(PayloadLoader.loadSchema(apiHandler.getEndpoint().getName())));
    }

    @Wtedy("^response matches schema (.+?)$")
    public void responseMatchesSchema(String name) {
        apiHandler.getResponse().then().assertThat().body(matchesJsonSchema(PayloadLoader.loadSchema(name)));
    }
}
