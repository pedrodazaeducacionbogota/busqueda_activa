package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.List;

import lombok.Data;

/**
 * Etapa 2 (HU-005): solicitud de cupo educativo + hermanos.
 * Reutiliza SolicitudEntity + SolicitudColegioEntity (standalone BA, no dispara inscripción).
 */
@Data
public class BAEtapa2Dto {

    /** Código último año aprobado (descripcion=GRADOS_ESCOLARES). */
    private String codigoUltimoAnioAprobado;

    /** Localidad de la institución (descripcion=LOCALIDAD-PRUEBA). Filtro front, no se persiste. */
    private String codigoLocalidadInstitucion;

    /** IDs de IDE (instituciones) en orden de preferencia. Máx 10. La posición define orden_preferencia. */
    private List<Long> idsColegios;

    /** Código grado solicitado (descripcion=GRADOS_ESCOLARES). Inmediatamente siguiente al aprobado. */
    private String codigoGradoSolicitaCupo;

    // -------- Hermano (opcional) --------
    private boolean tieneHermano;
    private String codigoTipoDocumentoHermano;
    private String numeroDocumentoHermano;
    private String primerNombreHermano;
    private String segundoNombreHermano;
    private String primerApellidoHermano;
    private String segundoApellidoHermano;
    /** ¿Desea misma institución que el hermano? */
    private boolean mismaInstitucionHermano;
    /** ID IDE donde está el hermano. */
    private Long idInstitucionHermano;
}
