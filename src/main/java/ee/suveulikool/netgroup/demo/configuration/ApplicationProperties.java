package ee.suveulikool.netgroup.demo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("net.group")
@Component
@Data
public class ApplicationProperties {
    public static Integer maxDepth = 10; // Total dockers running same time
}
