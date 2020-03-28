package ee.suveulikool.netgroup.demo.repository;

import ee.suveulikool.netgroup.demo.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findTop500ByOrderByIdDesc();

    List<Person> findByName(@Param("name") String name);

    Optional<Person> findByCountryCodeAndIdCode(@Param("country_code") String country_code, @Param("id_code") String id_code);
}
