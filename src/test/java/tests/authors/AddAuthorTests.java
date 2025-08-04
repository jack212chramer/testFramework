package tests.authors;

import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.getPayload;
import static core.PayloadLoader.loadSchema;
import static dictionaries.MyConstants.FIRST_NAME;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.ID_BOOK;
import static dictionaries.MyConstants.LAST_NAME;
import static dictionaries.MyConstants.PARSE_TO_INT_ERROR_MESSAGE;
import static dictionaries.Schemas.AUTHOR_DETAILS;
import static dictionaries.Schemas.EMPTY;
import static dictionaries.Schemas.ERROR;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS_ID;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;
import static utils.Helpers.getRandomNumber;

@Tag("AddAuthor")
@Tag("POST")
public class AddAuthorTests extends BaseTest {

    @Test
    @DisplayName("Add new author")
    public void addNewAuthor() {
        api.setEndpoint(AUTHORS)
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_DETAILS))))
                .and()
                .body(FIRST_NAME, equalTo("Jan"))
                .and()
                .body(LAST_NAME, equalTo("Kowalski"));
    }

    @Test
    @DisplayName("Add new author - persistence verification")
    @Description("This scenario verifies if the book added in the previous test is persisted in the database." +
            "I won't repeat such tests for every other endpoint because this api is not persistent.")
    public void addNewAuthorPersistenceCheck() {
        String uniqueId = valueOf(getRandomNumber(10000, 90000));
        String idBook = "10";
        api.setEndpoint(AUTHORS)
                .setParams(Map.of(ID, uniqueId, ID_BOOK, idBook))
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(200);
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, uniqueId))
                .get(Map.of())
                .then()
                .statusCode(200)
                .and()
                .body(ID_BOOK, equalTo(idBook));
    }

    @Test
    @DisplayName("Add new author - empty payload")
    @Description("This scenario fails because the API does not validate if the body is empty.")
    public void addNewAuthorEmptyPayload() {
        api.setEndpoint(AUTHORS)
                .post(getPayload(valueOf(EMPTY)))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Add new author - empty book id parameter in body")
    public void addNewAuthorWithLackingTitle() {
        api.setEndpoint(AUTHORS)
                .setParams(Map.of(ID_BOOK, ""))
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }

    @Test
    @DisplayName("Add new book - invalid method")
    public void addNewBookInvalidMethod() {
        api.setEndpoint(AUTHORS)
                .put(getPayload(AUTHORS))
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("Add an author with existing id")
    public void addNewAuthorWithExistingId() {
        api.setEndpoint(AUTHORS)
                .setParams(Map.of(ID, "1"))
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(409)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }

    @Test
    @DisplayName("Add author with incorrect id format")
    public void addNewAuthorWithIncorrectIdFormat() {
        api.setEndpoint(AUTHORS)
                .setParams(Map.of(ID, "A"))
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_INT_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_INT_ERROR_MESSAGE);
    }
}
