package ee.suveulikool.netgroup.demo;

import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = DemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersonControllerTests {

    @LocalServerPort
    private int port;

    @Before
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void putPersonThenUpdateThenGetPersonByNameReturnsPerson() {

        PersonRequestDto person = PersonRequestDto.builder()
                .name("Test")
                .countryCode("EST")
                .idCode("49h8g64yj5")
                .build();

        given()
                .when()
                .body(person)
                .contentType("application/json")
                .post("api/v1/person")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        person.setName("New Name");

        given()
                .when()
                .body(person)
                .contentType("application/json")
                .put("api/v1/person")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        Person[] people = given()
                .when()
                .get("api/v1/person/New Name")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert people.length != 0;
        assert people[0].getName().equals("New Name");

    }

}
