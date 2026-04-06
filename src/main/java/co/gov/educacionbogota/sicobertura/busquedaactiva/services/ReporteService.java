package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import java.io.ByteArrayInputStream;

public interface ReporteService {
    ByteArrayInputStream generarReportePdf(RegistroBusquedaActiva registro);
}
