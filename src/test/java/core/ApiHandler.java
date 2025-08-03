package core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiHandler {

    private static ApiHandler instance;
    public static ApiHandler getInstance() {
        if (instance == null) {
            instance = new ApiHandler();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private ApiHandler() {
        // Private constructor to enforce singleton pattern
    }

    private final ApiActions apiActions = new ApiActions();

    public ApiActions Actions() {
        return apiActions;
    }
}
