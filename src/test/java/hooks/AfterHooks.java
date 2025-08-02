package hooks;

import core.ApiActions;
import core.ApiHandler;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.bs.A;
import org.apache.commons.lang3.StringUtils;

public class AfterHooks {
    ApiActions apiActions = ApiActions.getInstance();

    @After
    public void doAfterEachScenario(Scenario scenario) {
        scenario.log("\n------------Api-calls----------------------------\n");
        scenario.log(StringUtils.join(apiActions.getApiHandler().getApiCalls(), "\n-------------------------------------------------\n"));
        scenario.log("\n------------Remembered-params--------------------\n");
        apiActions.getApiHandler().getRememberedParams().forEach((key, value) -> scenario.log(key + " = " + value));
        ApiActions.resetInstance();
    }
}