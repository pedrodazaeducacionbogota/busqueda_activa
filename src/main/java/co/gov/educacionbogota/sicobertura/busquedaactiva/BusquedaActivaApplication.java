package co.gov.educacionbogota.sicobertura.busquedaactiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"co.gov.educacionbogota.sicobertura"})
@EntityScan(basePackages = {"co.gov.educacionbogota.sicobertura.entities", "co.gov.educacionbogota.sicobertura.busquedaactiva.entities"})
public class BusquedaActivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusquedaActivaApplication.class, args);
    }
}
