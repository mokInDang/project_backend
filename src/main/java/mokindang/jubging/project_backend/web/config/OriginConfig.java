package mokindang.jubging.project_backend.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OriginConfig implements WebMvcConfigurer {

    private static final String LOCAL_HOST = "http://localhost:8080";

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(LOCAL_HOST);
    }
}
