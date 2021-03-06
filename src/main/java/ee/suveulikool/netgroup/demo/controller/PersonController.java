package ee.suveulikool.netgroup.demo.controller;

import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.exception.PersonValidationException;
import ee.suveulikool.netgroup.demo.service.PersonService;
import ee.suveulikool.netgroup.demo.service.PersonServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/person")
public class PersonController {

    private PersonService personService;

    public PersonController(PersonServiceImpl personService) {
        this.personService = personService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{name}")
    public List<Person> getPerson(@PathVariable("name") String name) {
        return personService.getPersonByName(name);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/tree/{countryCode}/{idCode}")
    public Optional<Person> getPersonTree(@PathVariable String countryCode, @PathVariable String idCode) {
        return personService.getPersonByCountryCodeAndIDCodeAsTree(countryCode, idCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{countryCode}/{idCode}")
    public Optional<Person> getPerson(@PathVariable String countryCode, @PathVariable String idCode) {
        return personService.getPersonByCountryCodeAndIDCode(countryCode, idCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/all")
    public List<Person> getPeople() {
        return personService.getPeople();
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{countryCode}/{idCode}")
    public void updatePerson(@PathVariable String countryCode, @PathVariable String idCode, @RequestBody PersonRequestDto requestDto) throws PersonNotFoundException, PersonExistsException, PersonValidationException {
        personService.updatePerson(countryCode, idCode, requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "")
    public void createPerson(@RequestBody PersonRequestDto requestDto) throws PersonNotFoundException, PersonExistsException, PersonValidationException {
        personService.createPerson(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{countryCode}/{idCode}")
    public void deletePerson(@PathVariable String countryCode, @PathVariable String idCode) throws PersonNotFoundException {
        personService.deletePersonByCountryCodeAndIDCode(countryCode, idCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/position/{countryCode}/{idCode}")
    public Integer getPersonsPositionInFamily(@PathVariable String countryCode, @PathVariable String idCode) throws PersonNotFoundException {
        return personService.getPersonPositionInFamily(countryCode, idCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/youngest/auntOrUncle")
    public Optional<Person> getYoungestAuntOrUncle() {
        return personService.getYoungestAuntOrUncle();
    }

}
