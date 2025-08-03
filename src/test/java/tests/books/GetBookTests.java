package tests.books;

import core.PayloadLoader;
import dictionaries.Schemas;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import java.util.Map;

import static core.endpoints.BooksEndpoint.BOOKS;
import static core.endpoints.BooksEndpoint.BOOKS_ID;
import static dictionaries.MyConstants.ID;
import static dictionaries.MyConstants.TITLE;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.equalTo;

@Tag("GetBook")
@Tag("GET")
public class GetBookTests extends BaseTest {

    @Test
    @DisplayName("Get Book List")
    @Description("Test to retrieve the list of books and validate the response schema.")
    public void getBookList() {
        api.setEndpoint(BOOKS)
                .get(Map.of())
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(PayloadLoader.loadSchema(String.valueOf(Schemas.BOOK_LIST))));
    }

    @Test
    @DisplayName("Get book by ID")
    public void getBookById() {
        api.setParams(Map.of(ID, "1"))
                .setEndpoint(BOOKS_ID)
                .get(Map.of())
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(PayloadLoader.loadSchema(String.valueOf(Schemas.BOOK_DETAILS))));
    }

    @Test
    @DisplayName("Get book by ID in query param instead of path param")
    @Description("Test to retrieve a book by ID using query parameters instead of path parameters - the param should be ignored and list of books should be returned.")
    public void getBookByQueryParam() {
        api.setEndpoint(BOOKS)
                .get(Map.of(ID, "1"))
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body(matchesJsonSchema(PayloadLoader.loadSchema(String.valueOf(Schemas.BOOK_LIST))));
    }

    @Test
    @DisplayName("Get book by non existing ID")
    public void getBookByNonExistingId() {
        api.setParams(Map.of(ID, "-1"))
                .setEndpoint(BOOKS_ID)
                .get(Map.of())
                .then()
                .statusCode(404)
                .and()
                .assertThat()
                .body(matchesJsonSchema(PayloadLoader.loadSchema(String.valueOf(Schemas.ERROR))))
                .and()
                .body(TITLE, equalTo("Not Found"));
    }

    @Test
    @DisplayName("Get book by ID - sql injection")
    public void getBookByIdSqlInjection() {
        api.setEncodePathParams(false);
        api.setParams(Map.of(ID, "' OR 1=1;--"))
                .setEndpoint(BOOKS_ID)
                .get(Map.of())
                .then()
                .statusCode(400)
                .and()
                .assertThat()
                .body(matchesJsonSchema(PayloadLoader.loadSchema(String.valueOf(Schemas.ERROR))));
    }
}
