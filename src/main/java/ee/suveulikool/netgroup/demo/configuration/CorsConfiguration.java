package ee.suveulikool.netgroup.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "*" // allow all. Add JWT when logging in is needed. Out of the scope ATM
                        ).allowedMethods("PUT", "DELETE", "GET", "POST")
                        .allowCredentials(true);
            }

        };
    }
}