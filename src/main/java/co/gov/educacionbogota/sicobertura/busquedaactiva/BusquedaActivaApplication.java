package co.gov.educacionbogota.sicobertura.busquedaactiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"co.gov.educacionbogota.sicobertura"})
@EntityScan(basePackages = {"co.gov.educacionbogota.sicobertura.entities", "co.gov.educacionbogota.sicobertura.busquedaactiva.entities"})
@EnableJpaRepositories(basePackages = {"co.gov.educacionbogota.sicobertura.repository", "co.gov.educacionbogota.sicobertura.busquedaactiva.repositories"})
public class BusquedaActivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusquedaActivaApplication.class, args);
    }
}
