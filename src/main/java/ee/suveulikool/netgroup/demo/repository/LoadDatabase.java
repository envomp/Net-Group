package ee.suveulikool.netgroup.demo.repository;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.service.PersonServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initUserDatabase(PersonServiceImpl personServiceImpl) {
        GeneratePeople generatePeople = new GeneratePeople().invoke();
        Person child = generatePeople.getChild();
        Person sister = generatePeople.getSister();
        Person half_sister = generatePeople.getHalf_sister();
        Person father = generatePeople.getFather();
        Person mother = generatePeople.getMother();
        Person aunt = generatePeople.getAunt();
        Person grand_mother = generatePeople.getGrand_mother();

        return args -> {

            personServiceImpl.createPerson(child);
            personServiceImpl.createPerson(sister);
            personServiceImpl.createPerson(half_sister);
            personServiceImpl.createPerson(father);
            personServiceImpl.createPerson(mother);
            personServiceImpl.createPerson(aunt);
            personServiceImpl.createPerson(grand_mother);

            generatePeople.addRelations();

            personServiceImpl.updatePerson(child);
            personServiceImpl.updatePerson(sister);
            personServiceImpl.updatePerson(half_sister);
            personServiceImpl.updatePerson(father);
            personServiceImpl.updatePerson(mother);
            personServiceImpl.updatePerson(aunt);
            personServiceImpl.updatePerson(grand_mother);
        };
    }
}
