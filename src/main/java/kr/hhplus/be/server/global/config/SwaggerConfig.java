package kr.hhplus.be.server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce Mock API")
                        .description("Swagger 문서로 확인하는 Mock API")
                        .version("1.0.0"));
    }
}