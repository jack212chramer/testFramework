package core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiHandler {

    private static volatile ApiHandler instance;

    public static ApiHandler getInstance() {
        ApiHandler localInstance = instance;
        if (localInstance == null) {
            synchronized (ApiHandler.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = new ApiHandler();
                    instance = localInstance;
                }
            }
        }
        return localInstance;
    }

    public static void resetInstance() {
        synchronized (ApiHandler.class) {
            instance = null;
        }
    }

    private ApiHandler() {
        // Private constructor to enforce singleton pattern
    }

    private final ApiActions apiActions = new ApiActions();

    public ApiActions Actions() {
        return apiActions;
    }
}
