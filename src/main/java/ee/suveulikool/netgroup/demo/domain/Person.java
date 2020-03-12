package ee.suveulikool.netgroup.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"countryCode", "idCode"})})
public class Person {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String countryCode;

    @NotNull
    private String idCode;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Person> parents = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Person> children = new ArrayList<>();

    @NotNull
    private String name;

    private Long birthDate;

    private Gender gender;

    public enum Gender {
        MALE,
        FEMALE,
        UNDISCLOSED
    }

    @JsonIgnore
    @Builder.Default
    private boolean cut = false; // Once the graph is cut to a tree, no saving is possible. Also a marker for cutting the graph.

}
