package apiTests;

import apiTests.utils.FileNameConstants;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PutAPIRequest {

    @Test
    public void putAPIRequest() {

        try {
            String postAPIRequestBody =
                    FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY), "UTF-8");

            String putAPIRequestBody =
                    FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY), "UTF-8");

            String apiTokenRequestBody =
                    FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY), "UTF-8");

        // POST API
            Response response =

                    RestAssured
                            .given()
                                .contentType(ContentType.JSON)
                                .body(postAPIRequestBody)
                                .baseUri("https://restful-booker.herokuapp.com/booking")
                            .when()
                                .post()
                            .then().log().all()
                                .assertThat()
                                .statusCode(200)
                                .extract()
                                .response();

            String firstName= JsonPath.read(response.body().asString(), "$.booking.firstname");
            System.out.println(firstName);
            Assert.assertEquals(firstName, "abcd");

            int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");
            System.out.println("bookingId: "+ bookingId);

            // GET Call

            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                         .get("/{bookingId}", bookingId)
                    .then()
                        .assertThat()
                        .statusCode(200);

            // Token Generation

            Response tokenAPIResponse =
            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .body(apiTokenRequestBody)
                        .baseUri("https://restful-booker.herokuapp.com/auth")
                    .when()
                        .post()
                    .then().log().all()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response();

            String token = JsonPath.read(tokenAPIResponse.body().asString(), "$.token");
            System.out.println("Token: "+ token);

            // PUT API Request Call

            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .body(putAPIRequestBody)
                        .header("cookie", "token="+token)
                        .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                        .put("/{bookingId}", bookingId)
                    .then().log().all()
                        .assertThat()
                        .statusCode(200)
                        .body("firstname", Matchers.equalTo("abcd"));



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
