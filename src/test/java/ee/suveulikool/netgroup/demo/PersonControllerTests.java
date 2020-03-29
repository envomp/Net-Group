package ee.suveulikool.netgroup.demo;

import ee.suveulikool.netgroup.demo.api.request.PersonDto;
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

import java.util.ArrayList;
import java.util.List;

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

    PersonRequestDto person;

    PersonRequestDto relationshipBuddy;

    PersonDto relationshipBuddyDto;

    @Before
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        relationshipBuddy = PersonRequestDto.builder()
                .name("Test Relationship Buddy")
                .countryCode("EST")
                .idCode("429232416575")
                .build();

        relationshipBuddyDto = PersonDto.builder()
                .idCode("429232416575")
                .countryCode("EST")
                .build();

        person = PersonRequestDto.builder()
                .name("Test Person")
                .countryCode("EST")
                .idCode("49890760455")
                .children(new ArrayList<>(List.of(relationshipBuddyDto)))
                .build();
    }

    @Test
    public void stage1_GetNoResults() {

        Person[] people = given()
                .when()
                .get("api/v1/person/Test")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert people.length == 0;
    }

    @Test
    public void stage2_PostAndResultsExist() {

        given()
                .when()
                .body(relationshipBuddy)
                .contentType("application/json")
                .post("api/v1/person")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        given()
                .when()
                .body(person)
                .contentType("application/json")
                .post("api/v1/person")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        Person[] people = given()
                .when()
                .get("api/v1/person/Test Person")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert people.length != 0;
        assert people[0].getName().equals("Test Person");
        assert people[0].getChildren().get(0).getName().equals("Test Relationship Buddy");
    }

    @Test
    public void stage3_PutAndResultsExist() {
        person.setName("New Name");

        given()
                .when()
                .body(person)
                .contentType("application/json")
                .put("api/v1/person/" + person.getCountryCode() + "/" + person.getIdCode())
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        Person[] new_people = given()
                .when()
                .get("api/v1/person/New Name")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert new_people.length != 0;
        assert new_people[0].getName().equals("New Name");
    }

    @Test
    public void stage4_GetTree() {

        Person people = given()
                .when()
                .get("api/v1/person/tree/EST/49890760455")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person.class);

        assert people.getChildren().size() == 0;
        assert people.getParents().size() == 0;

        Person person2 = given()
                .when()
                .get("api/v1/person/tree/EST/429232416575")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person.class);

        assert person2.getChildren().size() == 0;
        assert person2.getParents().size() == 1;

    }

    @Test
    public void stage5_GetGraph() {

        Person people = given()
                .when()
                .get("api/v1/person/EST/49890760455")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person.class);

        assert people.getChildren().size() == 1;
        assert people.getChildren().get(0).getParents().size() == 0;
        assert people.getParents().size() == 0;

        Person person2 = given()
                .when()
                .get("api/v1/person/EST/429232416575")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person.class);

        assert person2.getChildren().size() == 0;
        assert person2.getParents().size() == 1;
        assert person2.getParents().get(0).getChildren().size() == 0;

    }

    @Test
    public void stage6_GetPeople() {

        Person[] people = given()
                .when()
                .get("api/v1/person/all")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert people.length == 3 + 6; // 6 existed before

        Person person2 = given()
                .when()
                .get("api/v1/person/youngest/auntOrUncle")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person.class);

        assert person2 != null; //There's this aunt
    }

    @Test
    public void stage7_DeleteAndGetNoResults() {

        given()
                .when()
                .contentType("application/json")
                .delete("api/v1/person/EST/49890760455")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        Person[] no_people = given()
                .when()
                .get("api/v1/person/New Name")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert no_people.length == 0;

    }

    @Test
    public void stage8_GetNotCascaded() {

        Person[] people = given()
                .when()
                .get("api/v1/person/Test Relationship Buddy")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert people.length != 0;
        assert people[0].getName().equals("Test Relationship Buddy");
        assert people[0].getChildren().isEmpty();
        assert people[0].getParents().isEmpty();
    }

    @Test
    public void stage9_DeleteAndGetNoResults() {

        given()
                .when()
                .contentType("application/json")
                .delete("api/v1/person/EST/429232416575")
                .then()
                .statusCode(is(HttpStatus.SC_OK));

        Person[] no_people = given()
                .when()
                .get("api/v1/person/Test Relationship Buddy")
                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .extract()
                .body()
                .as(Person[].class);

        assert no_people.length == 0;

    }

}
