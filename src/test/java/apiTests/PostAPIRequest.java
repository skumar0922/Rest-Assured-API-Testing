package apiTests;

import apiTests.utils.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;


@Test
public class PostAPIRequest extends BaseTest {

    public void createBooking() {

        String BaseURl = "https://restful-booker.herokuapp.com";
        //Prepare Request Body
        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkin", "2018-01-01");
        bookingDates.put("checkout", "2019-01-01");

        JSONObject booking = new JSONObject();
        booking.put("firstname", "xyz");
        booking.put("lastname", "abc"+Math.random());
        booking.put("totalprice", 1000);
        booking.put("depositpaid", true);
        booking.put("bookingdates", bookingDates);
        booking.put("additionalneeds", "super bowls");


Response response =
        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .baseUri(BaseURl)
                    .body(booking.toJSONString())
                  //  .log().all()// Logs the request Body
                .when()
                    .post("/booking")
                .then()
                    .assertThat()
                //    .log().ifValidationFails() // Logs if test fails
                    .statusCode(200)
                .body("booking.firstname", Matchers.equalTo("xyz"))
                .body("booking.totalprice", Matchers.equalTo(1000))
                .body("booking.bookingdates.checkin", Matchers.equalTo("2018-01-01"))
                .extract()
                .response();

// we can extract the response of an api and pass it as input to another api, this is called api chaining.

int bookingId = response.path("bookingid");

    RestAssured
            .given()
                .contentType(ContentType.JSON)
                .pathParams("bookingID", bookingId)
                .baseUri(BaseURl)
            .when()
                .get("/booking/{bookingID}")
            .then()
                .assertThat()
                .statusCode(200)
            .body("firstname", Matchers.equalTo("xyz"))
            .body("totalprice", Matchers.equalTo(1000))
            .body("lastname", Matchers.containsString("abc0"));

    }
}
