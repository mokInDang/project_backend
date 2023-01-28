package mokindang.jubging.project_backend;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:/application-test.yml")
class ProjectBackendApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertThat(1+1).isEqualTo(2);
    }

}
