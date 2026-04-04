package co.gov.educacionbogota.sicobertura.busquedaactiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"co.gov.educacionbogota.sicobertura.commons", "co.gov.educacionbogota.sicobertura.busquedaactiva"})
@EntityScan(basePackages = {"co.gov.educacionbogota.sicobertura.commons.entities", "co.gov.educacionbogota.sicobertura.busquedaactiva.entities"})
public class BusquedaActivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusquedaActivaApplication.class, args);
    }
}
