package ee.suveulikool.netgroup.demo.api.request;

import com.sun.istack.NotNull;
import ee.suveulikool.netgroup.demo.domain.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDto {

    @NotNull
    private String countryCode;

    @NotNull
    private String idCode;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PersonDto> parents = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PersonDto> children = new ArrayList<>();

    @NotNull
    private String name;

    private Date birthDate; // sql.date - epoch time

    private Date deathDate; // sql.date - epoch time

    private Person.Gender gender;

}
