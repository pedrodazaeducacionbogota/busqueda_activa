package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import java.util.List;
import lombok.Data;

/**
 * Etapa 2 (HU-005): solicitud de cupo educativo + hermanos.
 * Los campos ref_listado llegan como RefListadoKVDto {id, codigo, valorTxt, valorInt}.
 */
@Data
public class BAEtapa2Dto {

    /** Último año aprobado (descripcion=GRADOS_ESCOLARES). */
    private RefListadoKVDto ultimoAnioAprobado;

    /** Localidad de la institución (descripcion=LOCALIDAD-PRUEBA). Filtro front, no se persiste. */
    private String codigoLocalidadInstitucion;

    /** IDs de IDE (instituciones) en orden de preferencia. Máx 10. */
    private List<Long> idsColegios;

    /** Grado solicitado (descripcion=GRADOS_ESCOLARES). */
    private RefListadoKVDto gradoSolicitaCupo;

    // -------- Hermano (opcional) --------
    private boolean tieneHermano;
    private RefListadoKVDto tipoDocumentoHermano;
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
