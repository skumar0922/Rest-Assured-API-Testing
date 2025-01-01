package apiTests;
import apiTests.utils.FileNameConstants;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;

public class PostAPIRequestUsingTxtFile {

    @Test
    public void postAPIRequest() {

        try {
           String postAPIRequestBody =
                   FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY), "UTF-8");

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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
