package tests.authors;

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
import static dictionaries.Schemas.AUTHORS_LACKING_LASTNAME;
import static dictionaries.Schemas.AUTHOR_DETAILS;
import static dictionaries.Schemas.EMPTY;
import static dictionaries.Schemas.ERROR;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS_ID;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;

@Tag("UpdateAuthor")
@Tag("PUT")
public class UpdateAuthorTests extends BaseTest {
    @Test
    @DisplayName("Update an author with id")
    public void updateAuthorById() {
        String firstName = "Janusz";
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1", FIRST_NAME, firstName))
                .put(getPayload(AUTHORS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_DETAILS))))
                .and()
                .body(FIRST_NAME, equalTo(firstName));
    }

    @Test
    @DisplayName("Update an author - empty payload")
    public void updateAuthorEmptyPayload() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(valueOf(EMPTY)))
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Update an author - lacking lastname parameter in body")
    public void updateAuthorWithLackingLastName() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1"))
                .put(getPayload(valueOf(AUTHORS_LACKING_LASTNAME)))
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }

    @Test
    @DisplayName("Update an author - invalid book id format")
    public void updateAuthorWithInvalidIdBookFormat() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1", ID_BOOK, "ABC"))
                .put(getPayload(AUTHORS))
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
    @DisplayName("Update an author - invalid method")
    public void updateAuthorWithInvalidMethod() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1"))
                .post(getPayload(AUTHORS))
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("Update an author - non existing id")
    public void updateAuthorWithNonExistingId() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "-99"))
                .put(getPayload(AUTHORS))
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Update an author - non existing book id")
    public void updateAuthorWithNonExistingBookId() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1", ID_BOOK, "-999"))
                .put(getPayload(AUTHORS))
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Update n author - incorrect id format")
    public void updateAuthorWithNonIncorrectIdFormat() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "ABC"))
                .put(getPayload(AUTHORS))
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
    @DisplayName("Update an author - problematic characters")
    public void updateAuthorWithProblematicCharacters() {
        String firstName = "يناير";
        String lastName = "史密斯";
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1", FIRST_NAME, firstName, LAST_NAME, lastName))
                .put(getPayload(AUTHORS))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(AUTHOR_DETAILS))))
                .and()
                .body(FIRST_NAME, equalTo(firstName))
                .and()
                .body(LAST_NAME, equalTo(lastName));
    }
}
