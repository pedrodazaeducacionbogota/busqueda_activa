package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/** Sección 7: educativo + solicitud cupo (finaliza wizard). */
@Data
public class BASeccion7Dto {
    /** Código último año estudio ref_listado (descripcion=GRADOS_ESCOLARES). */
    private String codigoUltimoAnioEstudio;
    private boolean repitioUltimoAnio;
    /** Código veces repitió ref_listado (descripcion=VECES_REPITIO). null si !repitioUltimoAnio. */
    private String codigoVecesRepitio;
    private String codigoUltimoAnioAprobado;
    private Long ideSolicitaCupo;
    private String codigoGradoSolicitaCupo;
}
