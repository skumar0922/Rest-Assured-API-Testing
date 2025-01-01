package apiTests;

import apiTests.pojos.Booking;
import apiTests.pojos.BookingDates;
import apiTests.utils.FileNameConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class PostAPIRequestUsingPojos {
    // Serialization ; In simple words, serialization converts the java object to a JSON Object.
    // As the name suggests, deserialization works the opposite of serialization.
    @Test
    public void postAPIRequest() throws IOException {

        BookingDates bookingdates = new BookingDates("2023-03-25", "2023-03-30");

        Booking booking = new Booking("API Testing", "Rest", "BreakFast", 1000, true, bookingdates);

        // Serialization
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // read the schema file
           String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.JSON_SCHEMA), "UTF-8");

            // Serialization
            String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
            System.out.println("requestBody123: "+requestBody);

            // De-serialization
            Booking bookingdetails = objectMapper.readValue(requestBody, Booking.class);
            System.out.println("------ De-serialization -------");
            System.out.println("firstname: "+ bookingdetails.getFirstname());
            System.out.println("totalprice: "+bookingdetails.getTotalprice());
            System.out.println("checkin: "+bookingdetails.getBookingdates().getCheckin());
            System.out.println("checkout: "+bookingdetails.getBookingdates().getCheckout());

            System.out.println("-------------");

            Response response =
            RestAssured
                    .given().log().all()
                        .contentType(ContentType.JSON)
                        .baseUri("https://restful-booker.herokuapp.com/booking")
                        .body(requestBody) // Serialization of Request Body
                    .when()
                        .post()
                    .then()
                         .assertThat()
                         .statusCode(200)
                    .extract().response();


            int bookingId = response.path("bookingid");

            System.out.println("--- Get Request------");
            RestAssured
                    .given()
                        .contentType(ContentType.JSON)
                        .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                        .get("/{bookingId}", bookingId)
                    .then().log().all()
                        .assertThat()
                        .statusCode(200)
                        .body(JsonSchemaValidator.matchesJsonSchema(jsonSchema)); // verify json schema, jsonschemavalidator comes from the dependency

          //  System.out.println(" -- verify json schema ----");
          //  System.out.println(jsonSchema);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
