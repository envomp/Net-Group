package ee.suveulikool.netgroup.demo.service;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PersonService {

    List<Person> getPersonByName(String name);

    default Person constructATreeWithPersonAsRoot(Person person) {
        person.setChildren(new ArrayList<>());
        person.setParents(new ArrayList<>());
        return person;
    }

    Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code);

    void putAll(List<Person> people) throws PersonIsCutException;

    void putPerson(Person person) throws PersonIsCutException;
}
