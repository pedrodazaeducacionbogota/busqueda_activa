package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/** Sección 3: colegios + jardines cerca de la visita. */
@Data
public class BASeccion3Dto {
    private boolean colegiosCerca;
    private Long ideColegioCerca;
    private String colegioCercaCual;
    private boolean jardinesCerca;
    private Long ideJardinCerca;
    private String jardinCercaCual;
}
