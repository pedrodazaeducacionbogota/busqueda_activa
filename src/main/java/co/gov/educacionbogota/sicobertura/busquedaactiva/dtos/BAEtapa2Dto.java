package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.List;
import lombok.Data;

/**
 * Etapa 2 (HU-005): solicitud de cupo educativo + hermanos.
 * Campos ref_listado llegan como id_ref_listado (Long) directo.
 */
@Data
public class BAEtapa2Dto {

    private Long codigoUltimoAnioAprobado;

    /** Filtro front, no se persiste. */
    private Long codigoLocalidadInstitucion;

    /** IDs de IDE en orden de preferencia. Máx 10. */
    private List<Long> idsColegios;

    private Long codigoGradoSolicitaCupo;

    private boolean tieneHermano;
    private Long codigoTipoDocumentoHermano;
    private String numeroDocumentoHermano;
    private String primerNombreHermano;
    private String segundoNombreHermano;
    private String primerApellidoHermano;
    private String segundoApellidoHermano;
    private boolean mismaInstitucionHermano;
    private Long idInstitucionHermano;
}
