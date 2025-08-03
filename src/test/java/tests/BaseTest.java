package tests;

import core.ApiHandler;
import core.ApiActions;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;

public abstract class BaseTest {
    protected final ApiActions api = ApiHandler.getInstance().Actions();

    @AfterEach
    public void tearDown() {
        String log = "\n------------Api-calls----------------------------\n";
        log += StringUtils.join(api.getApiCalls(), "\n-------------------------------------------------\n");
        Allure.addAttachment("Console Log", log);
        ApiHandler.resetInstance();
    }
}
