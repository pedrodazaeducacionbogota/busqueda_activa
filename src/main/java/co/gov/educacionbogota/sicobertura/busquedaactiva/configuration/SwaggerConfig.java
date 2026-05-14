package co.gov.educacionbogota.sicobertura.busquedaactiva.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("APIs Busqueda Activa SI Cobertura")
                        .description("APIs del modulo de Formulario de Busqueda Activa - identificacion de poblacion desescolarizada (NNAJ) - SI Cobertura SED")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pedro Daza")
                                .email("pedro.daza@educacionbogota.gov.co")
                                .url("https://www.educacionbogota.edu.co/")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
