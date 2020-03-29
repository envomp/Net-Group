package ee.suveulikool.netgroup.demo.domain;

import com.sun.istack.NotNull;
import ee.suveulikool.netgroup.demo.configuration.ApplicationProperties;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Builder
public class QueuePerson {

    @NotNull
    private Person person;

    @Builder.Default
    @NotNull
    private int depth = ApplicationProperties.maxDepth;

    private Person origin;

}
