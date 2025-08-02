package core.endpoints;

import core.Endpoint;
import lombok.Getter;

@Getter
public enum AuthorsEndpoint implements Endpoint {
    GET_AUTHORS("api/v1/Authors");

    private final String url;

    AuthorsEndpoint(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name();
    }

}
