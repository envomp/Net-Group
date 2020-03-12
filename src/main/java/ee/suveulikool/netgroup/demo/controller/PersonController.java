package ee.suveulikool.netgroup.demo.controller;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.service.PersonService;
import ee.suveulikool.netgroup.demo.service.PersonServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/person")
public class PersonController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private PersonService personService;

    public PersonController(PersonServiceImpl personService) {
        this.personService = personService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/get/{name}")
    public List<Person> getHome(@PathVariable("name") String name) throws PersonNotFoundException {
        LOG.info("Getting person with name {}", name);

        try { // handle people with multiple names. Right now select first
            return personService.getPersonByName(name);
        } catch (Exception e) {
            throw new PersonNotFoundException(String.format("Person with name: %s was not found!", name));
        }

    }

}
