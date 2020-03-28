package ee.suveulikool.netgroup.demo.service;

import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.exception.PersonValidationException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PersonService {

    List<Person> getPeople();

    List<Person> getPersonByName(String name);

    default Person standardisePerson(Person person, int depth) { // default depth is 0
        person.setChildren(new ArrayList<>());
        person.setParents(new ArrayList<>());
        person.fillPostTransactionFields();
        return person;
    }

    Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code);

    void updatePerson(Person person) throws PersonIsCutException, PersonNotFoundException, PersonValidationException;

    void updatePerson(PersonRequestDto person) throws PersonExistsException, PersonNotFoundException, PersonValidationException;

    void createPerson(Person person) throws PersonIsCutException, PersonExistsException, PersonValidationException;

    void createPerson(PersonRequestDto person) throws PersonExistsException, PersonNotFoundException, PersonValidationException;

    void deletePerson(Person person) throws PersonNotFoundException;

    void deletePersonByCountryCodeAndIDCode(String countryCode, String idCode) throws PersonNotFoundException;



}
