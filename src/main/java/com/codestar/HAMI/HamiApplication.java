package com.codestar.HAMI;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class HamiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HamiApplication.class, args);
    }
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .name("jwt-authorization")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("Bearer");
    }
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication")
                )
                .components(new Components()
                        .addSecuritySchemes
                                ("Bearer Authentication", createAPIKeyScheme())
                );
    }
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).select().build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Hermes API")
                .description("API for developers")
                .termsOfServiceUrl("https://simplifyingtechcode.wordpress.com/")
                .licenseUrl("simplifyingtech@gmail.com").version("2.0").build();
    }

}
