package tests.books;

import dictionaries.Schemas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.PayloadLoader.loadSchema;
import static core.endpoints.FakeRestApiEndpoints.BOOKS_ID;
import static dictionaries.MyConstants.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

@Tag("DeleteBook")
@Tag("DELETE")
public class DeleteBookTests extends BaseTest {
    @Test
    @DisplayName("Delete a book")
    public void deleteBook() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "1"))
                .delete(Map.of())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Delete a book with non existing ID")
    public void deleteBookWithNonExistingId() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "-99"))
                .delete(Map.of())
                .then()
                .statusCode(404)
                .and()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
    }

    @Test
    @DisplayName("Delete a book with incorrect ID format")
    public void deleteBookWithIncorrectIdFormat() {
        api.setEndpoint(BOOKS_ID)
                .setParams(Map.of(ID, "ABC"))
                .delete(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(loadSchema(String.valueOf(Schemas.ERROR))));
        Assertions.assertTrue(api.getResponse()
                .body()
                .asString()
                .contains("The value 'ABC' is not valid."));
    }
}
