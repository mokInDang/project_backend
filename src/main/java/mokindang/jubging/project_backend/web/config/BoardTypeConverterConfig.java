package mokindang.jubging.project_backend.web.config;

import mokindang.jubging.project_backend.web.converter.BoardTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BoardTypeConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new BoardTypeConverter());
    }
}
