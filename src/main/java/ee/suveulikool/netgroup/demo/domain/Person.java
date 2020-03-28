package ee.suveulikool.netgroup.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonValidationException;
import ee.suveulikool.netgroup.demo.utils.PersonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"countryCode", "idCode"})})
public class Person {

    private static final String NAME_CHECK = "^[A-Z][-A-z]+( [A-Z][-A-z]+)+$";

    private static final String COUNTRY_CODE_CHECK = "^[A-Z]{3}$";

    private static final String ID_CODE_CHECK = "^[0-9]{8,20}$";

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "Country code must exist")
    @Size(min = 3, max = 3) // https://www.iban.com/country-codes
    private String countryCode;

    @NotEmpty(message = "ID code must exist")
    @Size(min = 8, max = 20) // https://en.wikipedia.org/wiki/National_identification_number
    private String idCode;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Person> parents = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Person> children = new ArrayList<>();

    @NotEmpty(message = "Name is needed")
    @Size(min = 5)
    private String name;

    private Date birthDate; // Epoch time constructor

    private Date deathDate; // Epoch time constructor

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Integer age = this.getCalculatedAge();

    @NotEmpty(message = "Gender is needed")
    private Gender gender;

    public enum Gender {
        MALE,
        FEMALE,
        UNDISCLOSED
    }

    @JsonIgnore
    @Builder.Default
    private boolean cut = false; // Once the graph is cut to a tree, no saving is possible. Also a marker for cutting the graph.

    @PrePersist
    private void preInsert() throws PersonValidationException, PersonIsCutException {
        businessRuleCheck();
        validationRuleCheck();
    }

    @PreUpdate
    private void preUpdate() throws PersonIsCutException, PersonValidationException {
        businessRuleCheck();
        validationRuleCheck();
    }

    private void businessRuleCheck() throws PersonIsCutException, PersonValidationException {
        if (cut) {
            throw new PersonIsCutException("Cant save a cut person.");
        }

        for (Person parent : parents) { // Date check
            if (parent.getBirthDate() == null || this.getBirthDate().before(parent.getBirthDate())) {
                throw new PersonValidationException(String.format("%s should be born after his/her/it's parent %s", this.getName(), parent.getName()));
            }
        }

        if (getBirthDate() != null && getDeathDate() != null && this.getBirthDate().after(getDeathDate())) {
            throw new PersonValidationException(String.format("Birth date: %s should be before death date: %s", this.getBirthDate(), getDeathDate()));
        }

        if (getBirthDate() != null && this.getBirthDate().after(new Date(System.currentTimeMillis()))) {
            throw new PersonValidationException(String.format("Birth date: %s should be before current time %s", this.getBirthDate(), new Date(System.currentTimeMillis())));
        }
    }

    private void validationRuleCheck() throws PersonValidationException {

        if (!this.getName().matches(NAME_CHECK)) {
            throw new PersonValidationException(String.format("%s should match regex %s", this.getName(), NAME_CHECK));
        }

        if (!this.getCountryCode().matches(COUNTRY_CODE_CHECK)) {
            throw new PersonValidationException(String.format("%s should match regex %s", this.getCountryCode(), COUNTRY_CODE_CHECK));
        }

        if (!this.getIdCode().matches(ID_CODE_CHECK)) {
            throw new PersonValidationException(String.format("%s should match regex %s", this.getIdCode(), ID_CODE_CHECK));
        }
    }

    @PreRemove
    private void preRemove() {
        for (Person p : this.getParents()) {
            p.getChildren().remove(this);
        }

        for (Person p : this.getChildren()) {
            p.getParents().remove(this);
        }
    }

    public void fillPostTransactionFields() {
        age = getCalculatedAge();
    }

    private Integer getCalculatedAge() {
        try {
            return Math.toIntExact(ChronoUnit.YEARS.between(
                    Objects.requireNonNull(birthDate).toLocalDate(),
                    (deathDate == null ? new Date(System.currentTimeMillis()) : deathDate).toLocalDate())
            );
        } catch (NullPointerException e) {
            return -1; // can't calculate age
        }
    }

    public Boolean isSibling(Person sibling) {
        return parents.containsAll(sibling.getParents()) && sibling.getParents().containsAll(parents);
    }

    public Boolean isHalfSibling(Person halfSibling) {
        return parents.stream().anyMatch(x -> x.getChildren().contains(halfSibling)) && !isSibling(halfSibling);
    }

    public Boolean isMother(Person child) {
        return getGender().equals(Gender.FEMALE) && children.contains(child);
    }

    public Boolean isFather(Person child) {
        return getGender().equals(Gender.MALE) && children.contains(child);
    }

    public Boolean isGrandMother(Person child) {
        return getGender().equals(Gender.FEMALE) && children.stream().anyMatch(x -> x.getChildren().contains(child));
    }

    public Boolean isGrandFather(Person child) {
        return getGender().equals(Gender.MALE) && children.stream().anyMatch(x -> x.getChildren().contains(child));
    }

    public Boolean isAncestor(Person root) {
        return PersonUtils.isAncestor(this, root, 10);
    }

    public Boolean isBloodRelated(Person person) {
        return PersonUtils.IsRelative(this, person, 3);
    }

    public Boolean isDistantlyBloodRelated(Person person) {
        return PersonUtils.IsRelative(this, person, 10);
    }

}
