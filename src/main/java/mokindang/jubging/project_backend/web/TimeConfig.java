package mokindang.jubging.project_backend.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    private static final String ASIA_SEOUL = "Asia/Seoul";

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(ASIA_SEOUL));
    }
}
