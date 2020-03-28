package ee.suveulikool.netgroup.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.suveulikool.netgroup.demo.api.request.PersonDto;
import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.domain.QueuePerson;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.repository.PersonRepository;
import ee.suveulikool.netgroup.demo.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private PersonRepository personRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> getPeople() {
        return personRepository.findTop500ByOrderByIdDesc()
                .stream()
                .map(x -> standardisePerson(x, 1))
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> getPersonByName(String name) {
        LOG.info("Getting person with name {}", name);
        return personRepository.findByName(name)
                .stream()
                .map(x -> standardisePerson(x, 10))
                .collect(Collectors.toList());
    }

    public Person standardisePerson(Person person, int depth) {
        PersonUtils.generateTreeWithPersonAsRoot(person, depth);
        return person;
    }

    @Override
    public void updatePerson(Person person) throws PersonIsCutException, PersonNotFoundException {
        LOG.info("Updating person with name {}", person.getName());

        if (person.isCut()) {
            throw new PersonIsCutException("Cant save a person who's cut!");
        }

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(person.getCountryCode(), person.getIdCode());
        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db. Try using Post method instead.");
        }

        personRepository.saveAndFlush(person);
    }

    @Override
    public void updatePerson(PersonRequestDto personDto) throws PersonNotFoundException {
        LOG.info("Updating person with name {}", personDto.getName());

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(personDto.getCountryCode(), personDto.getIdCode());

        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db. Try using Put method instead.");
        }

        Person person = personOptional.get();

        if (personDto.getBirthDate() != null) {
            person.setBirthDate(personDto.getBirthDate());
        }

        if (personDto.getBirthDate() != null) {
            person.setDeathDate(personDto.getDeathDate());
        }

        if (personDto.getCountryCode() != null) {
            person.setCountryCode(personDto.getCountryCode());
        }

        if (personDto.getIdCode() != null) {
            person.setIdCode(personDto.getIdCode());
        }

        if (personDto.getGender() != null) {
            person.setGender(personDto.getGender());
        }

        if (personDto.getName() != null) {
            person.setName(personDto.getName());
        }

        fillChildrenAndParents(personDto, person);

        personRepository.saveAndFlush(person);

    }

    @Override
    public void createPerson(Person person) throws PersonIsCutException, PersonExistsException {
        LOG.info("Creating person with name {}", person.getName());

        if (person.isCut()) {
            throw new PersonIsCutException("Can't save a person who's cut!");
        }

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(person.getCountryCode(), person.getIdCode());
        if (personOptional.isPresent()) {
            throw new PersonExistsException("Person seems to be in db already. Try using Put method instead.");
        }

        personRepository.saveAndFlush(person);
    }

    @Override
    public void createPerson(PersonRequestDto personDto) throws PersonExistsException, PersonNotFoundException {
        LOG.info("Creating person with name {}", personDto.getName());

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(personDto.getCountryCode(), personDto.getIdCode());
        if (personOptional.isPresent()) {
            throw new PersonExistsException("Person seems to be in db already. Try using Put method instead.");
        }

        Person person = Person.builder()
                .birthDate(personDto.getBirthDate())
                .deathDate(personDto.getDeathDate())
                .countryCode(personDto.getCountryCode())
                .gender(personDto.getGender())
                .idCode(personDto.getIdCode())
                .name(personDto.getName())
                .build();

        fillChildrenAndParents(personDto, person);

        personRepository.saveAndFlush(person);
    }

    @Override
    public void deletePerson(Person person) throws PersonNotFoundException {
        LOG.info("Deleting person with name {}", person.getName());

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(person.getCountryCode(), person.getIdCode());
        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db. Try using Post method instead.");
        }

        personRepository.delete(person);
    }

    @Override
    public void deletePersonByCountryCodeAndIDCode(String countryCode, String idCode) throws PersonNotFoundException {
        LOG.info("Deleting person with country code {} and id code {}", countryCode, idCode);

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(countryCode, idCode);

        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db to delete it.");
        }

        personRepository.deleteById(personOptional.get().getId());

    }

    private void fillChildrenAndParents(PersonRequestDto personDto, Person person) throws PersonNotFoundException {
        if (personDto.getChildren() != null) {
            person.setChildren(new ArrayList<>());
            for (PersonDto childDto : personDto.getChildren()) {
                Optional<Person> child = personRepository.findByCountryCodeAndIdCode(childDto.getCountryCode(), childDto.getIdCode());
                if (child.isEmpty()) {
                    throw new PersonNotFoundException(String.format("Child with country code %s and id %s was not found!", childDto.getCountryCode(), childDto.getIdCode()));
                }
                person.getChildren().add(child.get());
            }
        }

        if (person.getParents() != null) {
            person.setParents(new ArrayList<>());
            for (PersonDto parentDto : personDto.getParents()) {
                Optional<Person> parent = personRepository.findByCountryCodeAndIdCode(parentDto.getCountryCode(), parentDto.getIdCode());
                if (parent.isEmpty()) {
                    throw new PersonNotFoundException(String.format("Parent with country code %s and id %s was not found!", parentDto.getCountryCode(), parentDto.getIdCode()));
                }
                person.getChildren().add(parent.get());
            }
        }
    }

    @Override
    public Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code) {
        Optional<Person> person = personRepository.findByCountryCodeAndIdCode(county_code, id_code);
        return person.map(x -> standardisePerson(x, 10));
    }
}
