package study.qa.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import study.qa.model.lombok.LoginBodyLombokModel;
import study.qa.model.lombok.LoginResponseLombokModel;
import study.qa.model.pojo.LoginBodyPojoModel;
import study.qa.model.pojo.LoginResponsePojoModel;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedSelenoidTests {
    /*
        1. Make request to https://selenoid.autotests.cloud/status
        2. Get response
        3. Check total is 20
     */

    @Test
    void checkTotal() {
        get("https://selenoid.autotests.cloud/status")
                .then()
                .body("total", is(20));
    }

    @Test
    void checkTotalWithStatus() {
        get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithGiven() {
        given()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithLogs() {
        given()
                .log().all()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkTotalWithSomeLogs() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("total", is(20));
    }

    @Test
    void checkChromeVersion() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("browsers.chrome", hasKey("100.0"));
    }

    @Test
    void checkResponseBadPractice() {
        String expectedResponseString = "{\"total\":20,\"used\":1,\"queued\":0,\"pending\":0,\"browsers\":" +
                "{\"android\":{\"8.1\":{}},\"chrome\":{\"100.0\":{\"user1\":{\"count\":1,\"sessions\":" +
                "[{\"id\":\"30f7a49d5c56afec35d595154af57258\"," +
                "\"container\":\"5ab612a9bf76cc6b5f6a1fc518ed29756aba1e2efe17083b6b6e5b898a7521a9\"," +
                "\"containerInfo\":{\"id\":\"5ab612a9bf76cc6b5f6a1fc518ed29756aba1e2efe17083b6b6e5b898a7521a9\"," +
                "\"ip\":\"172.18.0.4\"},\"vnc\":true,\"screen\":\"1920x1080x24\",\"caps\":" +
                "{\"browserName\":\"chrome\",\"version\":\"100.0\",\"screenResolution\":\"1920x1080x24\"," +
                "\"enableVNC\":true,\"videoScreenSize\":\"1920x1080\",\"name\":\"Manual session\",\"labels\":" +
                "{\"manual\":\"true\"},\"sessionTimeout\":\"60m\"},\"started\":\"2023-02-27T17:52:27.211472143Z\"}]}}," +
                "\"99.0\":{}},\"chrome-mobile\":{\"86.0\":{}},\"firefox\":{\"97.0\":{},\"98.0\":{}},\"opera\":" +
                "{\"84.0\":{},\"85.0\":{}}}}\n";

        Response actualResponse = given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        assertEquals(expectedResponseString, actualResponse.asString());
    }

    @Test
    void checkResponseNotBadPractice() {
        Integer exxpectedTotal = 20;

        Integer actualResponse = given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().path("total");

        assertEquals(exxpectedTotal, actualResponse);
    }

     /*
        1. Make request to https://selenoid.autotests.cloud/wd/hub/status
        2. Get response
        3. Check value.ready is true
     */

    @Test
    void checkWdHubStatus401() {
        given()
                .log().uri()
                .when()
                .get("https://selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(401);
    }

    @Test
    void checkWdHubStatus() {
        given()
                .log().uri()
                .when()
                .get("https://user1:1234@selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("value.ready",  is(true));
    }

    @Test
    void checkWdHubWithAuthStatus() {
        given()
                .log().uri()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud/wd/hub/status")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("value.ready",  is(true));
    }

    @Test
    void loginWithPojoModelTest() {

        LoginBodyPojoModel loginBody = new LoginBodyPojoModel();
        loginBody.setEmail("eve.holt@reqres.in");
        loginBody.setPassword("cityslicka");

        LoginResponsePojoModel loginResponse = given()
                .log().uri()
                .log().body()
                .contentType(JSON)
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().as(LoginResponsePojoModel.class);

        assertThat(loginResponse.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
    }

    @Test
    void loginWithLombokModelTest() {

        LoginBodyLombokModel loginBody = new LoginBodyLombokModel();
        loginBody.setEmail("eve.holt@reqres.in");
        loginBody.setPassword("cityslicka");

        LoginResponseLombokModel loginResponse = given()
                .log().uri()
                .log().body()
                .contentType(JSON)
                .body(loginBody)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().as(LoginResponseLombokModel.class);

        assertThat(loginResponse.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
    }
}
