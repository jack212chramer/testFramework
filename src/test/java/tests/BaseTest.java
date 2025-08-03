package tests;

import core.ApiActions;
import core.ApiHandler;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;

public abstract class BaseTest {
    protected final ApiHandler api = ApiActions.getInstance().getApiHandler();

    @AfterEach
    public void tearDown() {
        String log = "\n------------Api-calls----------------------------\n";
        log += StringUtils.join(api.getApiCalls(), "\n-------------------------------------------------\n");
        Allure.addAttachment("Console Log", log);
        ApiActions.resetInstance();
    }
}
