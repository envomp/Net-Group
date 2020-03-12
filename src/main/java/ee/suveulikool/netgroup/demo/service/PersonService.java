package ee.suveulikool.netgroup.demo.service;

import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;

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

    void updatePerson(Person person) throws PersonIsCutException, PersonNotFoundException;

    void updatePerson(PersonRequestDto person) throws PersonExistsException, PersonNotFoundException;

    void createPerson(Person person) throws PersonIsCutException, PersonExistsException;

    void createPerson(PersonRequestDto person) throws PersonExistsException, PersonNotFoundException;

    void deletePerson(Person person) throws PersonNotFoundException;

    void deletePerson(PersonRequestDto person) throws PersonNotFoundException;

}
