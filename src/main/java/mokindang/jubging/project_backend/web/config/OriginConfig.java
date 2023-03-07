package mokindang.jubging.project_backend.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OriginConfig implements WebMvcConfigurer {

    private static final String LOCAL_HOST = "http://localhost:8080";

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(LOCAL_HOST)
                .allowedOrigins("https://dongnejupging.xyz");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedOrigin("*");
        config.setMaxAge(3600L);
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
