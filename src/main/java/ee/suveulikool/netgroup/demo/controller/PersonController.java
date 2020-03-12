package ee.suveulikool.netgroup.demo.controller;

import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.service.PersonService;
import ee.suveulikool.netgroup.demo.service.PersonServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    @PutMapping(path = "")
    public void updatePerson(@RequestBody PersonRequestDto requestDto) throws PersonNotFoundException, PersonExistsException {
        personService.updatePerson(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "")
    public void createPerson(@RequestBody PersonRequestDto requestDto) throws PersonNotFoundException, PersonExistsException {
        personService.createPerson(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "")
    public void deletePerson(@RequestBody PersonRequestDto requestDto) throws PersonNotFoundException {
        personService.deletePerson(requestDto);
    }

}
