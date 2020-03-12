package ee.suveulikool.netgroup.demo.repository;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.service.PersonServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initUserDatabase(PersonServiceImpl personServiceImpl) {
        Person child = Person.builder()
                .birthDate(321L)
                .countryCode("EST")
                .gender(Person.Gender.MALE)
                .idCode("33qa23as2")
                .name("Enrico Vompa")
                .build();

        Person father = Person.builder()
                .birthDate(221L)
                .countryCode("EST")
                .gender(Person.Gender.MALE)
                .idCode("23qa23a1212")
                .name("Father")
                .build();

        Person mother = Person.builder()
                .birthDate(121L)
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("23qa23as2")
                .name("Mother")
                .build();

        Person aunt = Person.builder()
                .birthDate(121L)
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("23q1223123")
                .name("Aunt")
                .build();

        Person grand_mother = Person.builder()
                .birthDate(12L)
                .countryCode("EST")
                .gender(Person.Gender.FEMALE)
                .idCode("13qa23as2")
                .name("Mothers mother")
                .build();

        return args -> {

            addRelations(child, father, mother, aunt, grand_mother);
            personServiceImpl.putPerson(child);

        };
    }

    private void addRelations(Person child, Person father, Person mother, Person aunt, Person grand_mother) {
        grand_mother.getChildren().addAll(new ArrayList<>(List.of(aunt, mother)));
        mother.getChildren().add(child);
        mother.getParents().add(grand_mother);
        aunt.getParents().add(grand_mother);
        father.getChildren().add(child);
        child.getParents().addAll(new ArrayList<>(List.of(father, mother)));
    }
}
