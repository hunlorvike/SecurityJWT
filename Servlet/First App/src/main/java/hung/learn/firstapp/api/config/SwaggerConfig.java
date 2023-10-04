package hung.learn.firstapp.api.config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host("http://localhost:8080") // Đặt URL gốc của API của bạn
                .select()
                .apis(RequestHandlerSelectors.basePackage("hung.learn.firstapp.api.servlets"))
                .paths(PathSelectors.any())
                .build();
    }
}

