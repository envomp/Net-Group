package ee.suveulikool.netgroup.demo.api.request;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {

    @NotNull
    private String countryCode;

    @NotNull
    private String idCode;

}
