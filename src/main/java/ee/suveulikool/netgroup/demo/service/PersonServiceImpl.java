package ee.suveulikool.netgroup.demo.service;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.domain.QueuePerson;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getPersonByName(String name) {
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

    public void putPerson(Person person) throws PersonIsCutException {
        if (person.isCut()) {
            throw new PersonIsCutException("Cant save a person who's cut!");
        }
        personRepository.saveAndFlush(person);
    }

    public Optional<Person> getPersonByCountryCodeAndIDCode(String county_code, String id_code) {
        Optional<Person> person = personRepository.findByCountryCodeAndIdCode(county_code, id_code);
        return person.map(this::constructATreeWithPersonAsRoot);

    }
}
