package br.com.gubee.interview.core.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gubee Interview API")
                        .description("API para gerenciamento de heróis - Projeto de entrevista júnior")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gubee")
                                .url("http://www.gubee.com.br")
                                .email("contato@gubee.com.br"))
                        .license(new License()
                                .name("GNU General Public License v3.0")
                                .url("https://www.gnu.org/licenses/gpl.txt")))
                .tags(List.of(
                        new Tag().name("Heroes").description("Operações relacionadas a heróis"),
                        new Tag().name("Power Stats").description("Operações relacionadas a estatísticas de poder")
                ));
    }
}