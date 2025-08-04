package tests.books;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.getPayload;
import static core.PayloadLoader.loadSchema;
import static dictionaries.endpoints.FakeRestApiEndpoints.BOOKS;
import static dictionaries.endpoints.FakeRestApiEndpoints.BOOKS_ID;
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
import static java.lang.String.valueOf;
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
                .body(matchesJsonSchema(loadSchema(valueOf(BOOK_DETAILS))))
                .and()
                .body(PAGECOUNT, equalTo(250));
    }

    @Test
    @DisplayName("Update a book - case sensitive title")
    public void updateBookTitleCaseSensitiveCheck() {
        String title = "The Great Gatsby";
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", TITLE, title))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(BOOK_DETAILS))))
                .and()
                .body(TITLE, equalTo(title));
    }

    @Test
    @DisplayName("Update a book - empty payload")
    public void updateBookEmptyPayload() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(valueOf(EMPTY)))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Update a book - lacking title parameter in body")
    public void updateBookWithLackingTitle() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(valueOf(BOOKS_LACKING_TITLE)))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
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
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
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
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
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
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
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
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_DATETIME_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_DATETIME_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Update a book - non existing publish date")
    public void updateBookWithNonExistingPublishDate() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", PUBLISH_DATE, "2025-02-30T14:26:57.0497392+00:00"))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(PARSE_TO_DATETIME_ERROR_MESSAGE), "Response does not contain " + PARSE_TO_DATETIME_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Update a book - weird ascii characters in title")
    public void updateBookWithWeirdAsciiCharactersInTitle() {
        String title = "🌈@';";
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1", TITLE, title))
                .put(getPayload(BOOKS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(BOOK_DETAILS))))
                .and()
                .body(TITLE, equalTo(title));
    }
}
