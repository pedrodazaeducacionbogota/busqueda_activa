package co.gov.educacionbogota.sicobertura.busquedaactiva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class StartupLogger {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void logProfile() {
        System.out.println("ACTIVE PROFILE: "
                + Arrays.toString(environment.getActiveProfiles()));
    }
}
