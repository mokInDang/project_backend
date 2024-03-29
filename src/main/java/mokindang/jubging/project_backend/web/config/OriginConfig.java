package mokindang.jubging.project_backend.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;

@Configuration
public class OriginConfig implements WebMvcConfigurer {

    private static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,DELETE,TRACE,OPTIONS,PATCH,PUT";

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://dongnejupging.xyz",
                        "https://www.dongnejupging.xyz",
                        "http://www.dongnejupging.xyz",
                        "http://dongnejupging.xyz",
                        "https://www.dongnejupging.xyz",
                        "http://www.dongnejupging.xyz",
                        "https://dev.dongnejupging.xyz/api/auth/logout",
                        "http://localhost:8080",
                        "http://localhost:3000",
                        "https://localhost:3000",
                        "https://dev.dongnejupging.xyz")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders(AUTHORIZATION, LOCATION);
    }
}
