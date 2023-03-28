package mokindang.jubging.project_backend.web.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("API - 우리동네줍깅")
                .description("API Description");

        Server server = new Server().url("http://localhost:8080");
        Server devServer = new Server().url("https://dev.dongnejupging.xyz");
        List<Server> servers = Arrays.asList(server, devServer);

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }
}
