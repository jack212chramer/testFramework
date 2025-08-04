package tests.authors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.loadSchema;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.INVALID_VALUE_ERROR_MESSAGE;
import static dictionaries.Schemas.ERROR;
import static dictionaries.endpoints.FakeRestApiEndpoints.AUTHORS_ID;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.lang.String.valueOf;

@Tag("DeleteAuthor")
@Tag("DELETE")
public class DeleteAuthorTests extends BaseTest {
    @Test
    @DisplayName("Delete an author")
    public void deleteBook() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "1"))
                .delete(Map.of())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Delete an author with non existing ID")
    public void deleteAuthorWithNonExistingId() {
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, "-99"))
                .delete(Map.of())
                .then()
                .statusCode(404)
                .and()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
    }

    @Test
    @DisplayName("Delete an author with incorrect ID format")
    public void deleteAuthorWithIncorrectIdFormat() {
        String invalidParam = "ABC";
        api.setEndpoint(AUTHORS_ID)
                .setParams(Map.of(ID, invalidParam))
                .delete(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(valueOf(ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains(String.format(INVALID_VALUE_ERROR_MESSAGE, invalidParam)));
    }
}
