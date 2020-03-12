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

    public List<Person> getPersonByName(String name) {
        LOG.info("Getting person with name {}", name);
        return personRepository.findByName(name)
                .stream()
                .map(this::constructATreeWithPersonAsRoot)
                .collect(Collectors.toList());
    }

    public Person constructATreeWithPersonAsRoot(Person person) {
        LinkedList<QueuePerson> queue = new LinkedList<>();
        queue.add(QueuePerson.builder().person(person).build());
        QueuePerson origin;

        while (queue.size() != 0) {
            origin = queue.poll();
            origin.getPerson().setCut(true);

            if (origin.getDepth() != 0) { // break condition

                for (Person child : origin.getPerson().getChildren()) {
                    if (!child.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(child)
                                .origin(origin.getPerson())
                                .direction(QueuePerson.Direction.UP)
                                .build());
                    }
                }

                for (Person parent : origin.getPerson().getParents()) {
                    if (!parent.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(parent)
                                .origin(origin.getPerson())
                                .direction(QueuePerson.Direction.DOWN)
                                .build());
                    }
                }

            } else {
                origin.getPerson().setParents(new ArrayList<>());
                origin.getPerson().setChildren(new ArrayList<>());
            }


            if (origin.getOrigin() != null) {
                if (origin.getDirection() == QueuePerson.Direction.UP) {
                    origin.getPerson().getParents().remove(origin.getOrigin());
                } else {
                    origin.getPerson().getChildren().remove(origin.getOrigin());
                }
            }
        }

        return person;
    }

    public void putAll(List<Person> people) throws PersonIsCutException {
        if (people.stream().anyMatch(Person::isCut)) {
            throw new PersonIsCutException("Cant save a person who's cut!");
        }

        personRepository.saveAll(people);
        personRepository.flush();
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
            throw new PersonExistsException("Person seems to be in db already. Try using Post method instead.");
        }

        Person person = Person.builder()
                .birthDate(personDto.getBirthDate())
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
    public void deletePerson(PersonRequestDto personDto) throws PersonNotFoundException {
        LOG.info("Deleting person with name {}", personDto.getName());

        Optional<Person> personOptional = personRepository.findByCountryCodeAndIdCode(personDto.getCountryCode(), personDto.getIdCode());

        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException("Can't find the person from db to delete it.");
        }

        personRepository.delete(personOptional.get());

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

    public Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code) {
        Optional<Person> person = personRepository.findByCountryCodeAndIdCode(county_code, id_code);
        return person.map(this::constructATreeWithPersonAsRoot);

    }
}
