package ee.suveulikool.netgroup.demo.repository;

import ee.suveulikool.netgroup.demo.domain.Person;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GeneratePeople {
    private Person child;
    private Person sister;
    private Person half_sister;
    private Person father;
    private Person mother;
    private Person aunt;
    private Person grand_mother;

    public Person getChild() {
        return child;
    }

    public Person getHalf_sister() {
        return half_sister;
    }

    public Person getSister() {
        return sister;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    public Person getAunt() {
        return aunt;
    }

    public Person getGrand_mother() {
        return grand_mother;
    }

    public GeneratePeople invoke() {
        child = Person.builder()
                .birthDate(new Date(922617108000L))
                .countryCode("EST")
                .gender(Person.Gender.MALE)
                .idCode("334658567408")
                .name("Enrico Vompa")
                .build();

        sister = Person.builder()
                .birthDate(new Date(952617108000L))
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("444658567408")
                .name("Some Sister")
                .build();

        half_sister = Person.builder()
                .birthDate(new Date(962617108000L))
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("434658567408")
                .name("Some Half-Sister")
                .build();

        father = Person.builder()
                .birthDate(new Date(822617108000L))
                .countryCode("EST")
                .gender(Person.Gender.MALE)
                .idCode("235745645212")
                .name("Some Father")
                .build();

        mother = Person.builder()
                .birthDate(new Date(822617108000L))
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("2312356345")
                .name("Some Mother")
                .build();

        aunt = Person.builder()
                .birthDate(new Date(852617108000L))
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("23586456785467")
                .name("Some Aunt")
                .build();

        grand_mother = Person.builder()
                .birthDate(new Date(-722617108000L))
                .deathDate(new Date(922617108000L))
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("13345676458")
                .name("Mothers Mother")
                .build();
        return this;
    }

    public void addRelations() {
        grand_mother.getChildren().addAll(new ArrayList<>(List.of(aunt, mother)));
        mother.getChildren().addAll(new ArrayList<>(List.of(child, sister, half_sister)));
        father.getChildren().addAll(new ArrayList<>(List.of(child, sister)));
        mother.getParents().add(grand_mother);
        aunt.getParents().add(grand_mother);
        child.getParents().addAll(new ArrayList<>(List.of(father, mother)));
        sister.getParents().addAll(new ArrayList<>(List.of(father, mother)));
        half_sister.getParents().addAll(new ArrayList<>(List.of(mother)));
    }

}