package hooks;

import core.ApiActions;
import core.ApiHandler;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.bs.A;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;

public class AfterHooks {
    ApiActions apiActions = ApiActions.getInstance();

    @After
    public void doAfterEachScenario(Scenario scenario) {
        String log = "\n------------Api-calls----------------------------\n";
        log += StringUtils.join(apiActions.getApiHandler().getApiCalls(), "\n-------------------------------------------------\n");
//        scenario.log(log);
        if (scenario.isFailed()) {
            Allure.addAttachment("Console Log", log);
        }
        ApiActions.resetInstance();
    }
}