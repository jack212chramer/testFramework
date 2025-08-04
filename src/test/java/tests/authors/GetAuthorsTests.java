package tests.authors;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.loadSchema;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.INVALID_VALUE_ERROR_MESSAGE;
import static dictionaries.MyConstants.SQL_INJECTION_PAYLOAD_EXAMPLE;
import static dictionaries.MyConstants.TITLE;
import static dictionaries.Schemas.AUTHOR_DETAILS;
import static dictionaries.Schemas.AUTHOR_LIST;
import static dictionaries.Schemas.ERROR;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS_ID;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;

@Tag("GetAuthors")
@Tag("GET")
public class GetAuthorsTests extends BaseTest {

    @Test
    @DisplayName("Get Authors List")
    public void getAuthorsList() {
        api.setEndpoint(AUTHORS)
                .get(Map.of())
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_LIST))));
    }

    @Test
    @DisplayName("Get author by ID")
    public void getAuthorById() {
        api.setParams(Map.of(ID, "1"))
                .setEndpoint(AUTHORS_ID)
                .get(Map.of())
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_DETAILS))));
    }

    @Test
    @DisplayName("Get author by ID in query param instead of path param")
    @Description("Test to retrieve an author by ID using query parameters instead of path parameters - the param should be ignored and list of authors should be returned.")
    public void getAuthorByQueryParam() {
        api.setEndpoint(AUTHORS)
                .get(Map.of(ID, "1"))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_LIST))));
    }

    @Test
    @DisplayName("Get author by null ID")
    public void getAuthorByNullId() {
        String invalidParameter = "null";
        api.setParams(Map.of(ID, invalidParameter))
                .setEndpoint(AUTHORS_ID)
                .get(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                        .body()
                        .asString()
                        .contains(format(INVALID_VALUE_ERROR_MESSAGE, invalidParameter)),
                "Response does not contain " + format(INVALID_VALUE_ERROR_MESSAGE, invalidParameter));
    }

    @Test
    @DisplayName("Get author by non existing ID")
    public void getAuthorByNonExistingId() {
        api.setParams(Map.of(ID, "-1"))
                .setEndpoint(AUTHORS_ID)
                .get(Map.of())
                .then()
                .statusCode(404)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))))
                .and()
                .body(TITLE, equalTo("Not Found"));
    }

    @Test
    @DisplayName("Get author by invalid ID")
    public void getAuthorByInvalidId() {
        api.setParams(Map.of(ID, "true"))
                .setEndpoint(AUTHORS_ID)
                .get(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }

    @Test
    @DisplayName("Get author by ID - sql injection")
    public void getAuthorByIdSqlInjection() {
        api.setEncodePathParams(false);
        api.setParams(Map.of(ID, SQL_INJECTION_PAYLOAD_EXAMPLE))
                .setEndpoint(AUTHORS_ID)
                .get(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }
}
