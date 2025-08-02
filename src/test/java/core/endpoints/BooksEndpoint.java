package core.endpoints;

import core.Endpoint;
import lombok.Getter;

@Getter
public enum BooksEndpoint implements Endpoint {
    BOOKS("api/v1/Books"),
    BOOKS_ID("api/v1/Books/${id}");


    private final String url;

    BooksEndpoint(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name();
    }

}
