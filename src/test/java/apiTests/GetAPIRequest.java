package apiTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetAPIRequest {

    @Test
    public void getAllBookings() {

        Response response =
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .get()
                .then()
                    .assertThat()
                    .statusCode(200)
                    .statusLine("HTTP/1.1 200 OK")
                    .header("Content-Type", "application/json; charset=utf-8")
                .extract()
                    .response();

        Assert.assertTrue(response.body().asString().contains("bookingid"));


    }
}
