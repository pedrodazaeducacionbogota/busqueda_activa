package co.gov.educacionbogota.sicobertura.busquedaactiva.log;

import co.gov.educacionbogota.sicobertura.util.UtilGeneral;
import java.io.File;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LogInitializer {

    @PostConstruct
    public void init() {
        String rutaLog = UtilGeneral.obtenerRutaLog();
        System.setProperty("log.path", rutaLog);
        File ruta = new File(rutaLog);
        if (!ruta.getParentFile().exists()) {
            ruta.getParentFile().mkdirs();
        }
        System.out.println("Ruta de log configurada: " + rutaLog);
    }
}
