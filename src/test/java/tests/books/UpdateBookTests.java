package tests.books;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.getPayload;
import static core.PayloadLoader.loadSchema;
import static core.endpoints.FakeRestApiEndpoints.BOOKS;
import static core.endpoints.FakeRestApiEndpoints.BOOKS_ID;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.PAGECOUNT;
import static dictionaries.MyConstants.PARSE_TO_DATETIME_ERROR_MESSAGE;
import static dictionaries.MyConstants.PARSE_TO_INT_ERROR_MESSAGE;
import static dictionaries.MyConstants.PUBLISH_DATE;
import static dictionaries.MyConstants.TITLE;
import static dictionaries.Schemas.BOOKS_LACKING_TITLE;
import static dictionaries.Schemas.BOOK_DETAILS;
import static dictionaries.Schemas.EMPTY;
import static dictionaries.Schemas.ERROR;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;

@Tag("UpdateBook")
@Tag("PUT")
public class UpdateBookTests extends BaseTest {
    @Test
    @DisplayName("Update a book with id")
    public void updateBookById() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", PAGECOUNT, "250"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(BOOK_DETAILS))))
                .and()
                .body(PAGECOUNT, equalTo(250));
    }

    @Test
    @DisplayName("Update a book - empty payload")
    public void updateBookEmptyPayload() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(String.valueOf(EMPTY)))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Update a book - lacking title parameter in body")
    public void updateBookWithLackingTitle() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(String.valueOf(BOOKS_LACKING_TITLE)))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(ERROR))));
    }

    @Test
    @DisplayName("Update a book - invalid page count format")
    public void updateBookWithInvalidPageCountFormat() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", PAGECOUNT, "ABC"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_INT_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_INT_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Update a book - negative page count")
    public void updateBookWithNegativePageCount() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", PAGECOUNT, "-99"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(ERROR))));
    }

    @Test
    @DisplayName("Update a book - invalid method")
    public void updateBookWithInvalidMethod() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .post(getPayload(BOOKS))
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("Update a book - non existing id")
    public void updateBookWithNonExistingId() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "-99"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Update a book - incorrect id format")
    public void updateBookWithNonIncorrectIdFormat() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "ABC"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_INT_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_INT_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Update a book - incorrect timestamp")
    public void updateBookWithNonIncorrectTimestamp() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", PUBLISH_DATE, "NOW"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_DATETIME_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_DATETIME_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Update a book - weird ascii characters in title")
    public void updateBookWithWeirdAsciiCharactersInTitle() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", TITLE, "🌈@';"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(BOOK_DETAILS))))
                .and()
                .body(TITLE, equalTo("🌈@';"));
    }
}
