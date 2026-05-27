package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response validación documento (HU-004 paso 5). Dedup por documento + etapa + vigencia.
 * Si ya existe, el modal front muestra fecha + profesional que registró.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDocumentoDto {
    /** true = no existe formulario previo, puede continuar. */
    private boolean nuevo;
    /** Si nuevo=false, id del formulario existente. */
    private Long idFormulario;
    /** Fecha/hora del registro existente (yyyy-MM-dd HH:mm). */
    private String fechaRegistro;
    /** Profesional que realizó el registro existente. */
    private String registradoPor;
}
