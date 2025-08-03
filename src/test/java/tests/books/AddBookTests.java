package tests.books;

import dictionaries.Schemas;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.getPayload;
import static core.PayloadLoader.loadSchema;
import static core.endpoints.FakeRestApiEndpoints.BOOKS;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.PAGECOUNT;
import static dictionaries.MyConstants.PARSE_TO_DATETIME_ERROR_MESSAGE;
import static dictionaries.MyConstants.PARSE_TO_INT_ERROR_MESSAGE;
import static dictionaries.MyConstants.PUBLISH_DATE;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;

@Tag("AddBook")
@Tag("POST")
public class AddBookTests extends BaseTest {

    @Test
    @DisplayName("Add new book")
    public void addNewBook() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(PAGECOUNT, "99"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.BOOK_DETAILS))))
                .and()
                .body(PAGECOUNT, equalTo(99));
    }

    @Test
    @DisplayName("Add new book - empty payload")
    @Description("This scenario fails beecause the API does not validate if the body is empty.")
    public void addNewBookEmptyPayload() {
        api.setEndpoint(BOOKS)
                .post(getPayload(String.valueOf(Schemas.EMPTY)))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Add new book - lacking title parameter in body")
    public void addNewBookWithLackingTitle() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(PAGECOUNT, "99"))
                .post(getPayload(String.valueOf(Schemas.BOOKS_LACKING_TITLE)))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
    }

    @Test
    @DisplayName("Add new book with negative page count")
    public void addNewBookNegativePageCount() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(PAGECOUNT, "-99"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
    }

    @Test
    @DisplayName("Add new book with invalid page count format")
    public void addNewBookInvalidPageCount() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(PAGECOUNT, "ABC"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_INT_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_INT_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Add new book - invalid method")
    public void addNewBookInvalidMethod() {
        api.setEndpoint(BOOKS)
                .put(getPayload(BOOKS))
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("Add book with existing id")
    public void addNewBookWithExistingId() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(ID, "1"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(409)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
    }

    @Test
    @DisplayName("Add book with incorrect id format")
    public void addNewBookWithIncorrectIdFormat() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(ID, "A"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_INT_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_INT_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Add book with incorrect timestamp")
    public void addNewBookWithIncorrectTimestamp() {
        api.setEndpoint(BOOKS)
                .setParams(Map.of(PUBLISH_DATE, "NOW"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_DATETIME_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_DATETIME_ERROR_MESSAGE);
    }
}
