package ee.suveulikool.netgroup.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.suveulikool.netgroup.demo.api.request.PersonDto;
import ee.suveulikool.netgroup.demo.api.request.PersonRequestDto;
import ee.suveulikool.netgroup.demo.configuration.ApplicationProperties;
import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.repository.PersonRepository;
import ee.suveulikool.netgroup.demo.utils.PersonUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .map(x -> standardisePerson(x, ApplicationProperties.maxDepth))
                .collect(Collectors.toList());
    }

    public Person standardisePerson(Person person, int depth) {
        PersonUtils.generateGraphWithPersonAsRoot(person, depth);
        return person;
    }

    public Person standardisePersonIntoTree(Person person, int depth) {
        PersonUtils.generateTreeWithPersonAsRoot(person, depth);
        for (Person child : person.getChildren()) {
            child.setCut(true);
            child.getParents().remove(person);
        }
        person.setCut(true);
        person.setChildren(new ArrayList<>());
        return person;
    }

    @Override
    public void updatePerson(Person person) throws PersonNotFoundException {
        LOG.info("Updating person with name {}", person.getName());

        person.preTransaction();

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(person.getCountryCode(), person.getIdCode());
        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db. Try using Post method instead.");
        }

        personRepository.saveAndFlush(person);
    }

    @Override
    public void updatePerson(String countryCode, String idCode, PersonRequestDto personDto) throws PersonNotFoundException {
        LOG.info("Updating person with name {}", personDto.getName());

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(countryCode, idCode);

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

        person.preTransaction();

        personRepository.saveAndFlush(person);

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
    @SneakyThrows
    public void createPerson(PersonRequestDto personDto) {
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

        personRepository.saveAndFlush(person);

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

    @Override
    public Integer getPersonPositionInFamily(String countryCode, String idCode) throws PersonNotFoundException {
        LOG.info("Getting persons position with country code {} and id code {}", countryCode, idCode);

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(countryCode, idCode);

        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db to delete it.");
        }

        return Stream.concat(
                personOptional.get().getParents().stream()
                        .map(Person::getChildren).flatMap(Collection::stream), Stream.of(personOptional.get()))
                .collect(Collectors.toSet()).stream()
                .sorted(Comparator.comparing(Person::getBirthDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList()).indexOf(personOptional.get()) + 1;

    }

    private void fillChildrenAndParents(PersonRequestDto personDto, Person person) throws PersonNotFoundException {
        if (personDto.getChildren() != null) {

            for (Person child : person.getChildren()) {
                personRepository.saveAndFlush(child);
                child.getParents().remove(person);
            }
            person.setChildren(new ArrayList<>());

            for (PersonDto childDto : personDto.getChildren()) {
                Optional<Person> child = personRepository.findByCountryCodeAndIdCode(childDto.getCountryCode(), childDto.getIdCode());
                if (child.isEmpty()) {
                    throw new PersonNotFoundException(String.format("Child with country code %s and id %s was not found!", childDto.getCountryCode(), childDto.getIdCode()));
                }
                person.getChildren().add(child.get());
                child.get().getParents().add(person);
                personRepository.saveAndFlush(child.get());
            }
        }

        if (personDto.getParents() != null) {
            for (Person parent : person.getParents()) {
                personRepository.saveAndFlush(parent);
                parent.getChildren().remove(person);
            }
            person.setParents(new ArrayList<>());
            for (PersonDto parentDto : personDto.getParents()) {
                Optional<Person> parent = personRepository.findByCountryCodeAndIdCode(parentDto.getCountryCode(), parentDto.getIdCode());
                if (parent.isEmpty()) {
                    throw new PersonNotFoundException(String.format("Parent with country code %s and id %s was not found!", parentDto.getCountryCode(), parentDto.getIdCode()));
                }
                person.getParents().add(parent.get());
                parent.get().getChildren().add(person);
                personRepository.saveAndFlush(parent.get());
            }
        }
    }

    @Override
    public Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code) {
        Optional<Person> person = personRepository.findByCountryCodeAndIdCode(county_code, id_code);
        return person.map(x -> standardisePerson(x, ApplicationProperties.maxDepth));
    }

    @Override
    public Optional<Person> getPersonByCountryCodeAndIDCodeAsTree(String county_code, String id_code) {
        Optional<Person> person = personRepository.findByCountryCodeAndIdCode(county_code, id_code);
        return person.map(x -> standardisePersonIntoTree(x, ApplicationProperties.maxDepth));
    }

}
