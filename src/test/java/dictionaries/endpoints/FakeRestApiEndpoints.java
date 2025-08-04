package dictionaries.endpoints;

import core.Endpoint;
import lombok.Getter;

@Getter
public enum FakeRestApiEndpoints implements Endpoint {
    BOOKS("api/v1/Books"),
    BOOKS_ID("api/v1/Books/${id}"),
    AUTHORS("api/v1/Authors"),
    AUTHORS_ID("api/v1/Authors/${id}");



    private final String url;

    FakeRestApiEndpoints(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name();
    }

}
