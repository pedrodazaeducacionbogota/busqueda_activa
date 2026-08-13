package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response validación documento (HU-004 paso 5). Dedup por documento + etapa + vigencia
 * y bloqueo si estudiante ya matriculado SIMAT (Anexo6A).
 * Si ya existe formulario BA, el modal front muestra fecha + profesional que registró.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDocumentoDto {
    /** true = puede continuar (no existe formulario previo NI matriculado SIMAT). */
    private boolean nuevo;
    /** Si nuevo=false por dedup BA, id del formulario existente. */
    private Long idFormulario;
    /** Fecha/hora del registro existente (yyyy-MM-dd HH:mm). */
    private String fechaRegistro;
    /** Profesional que realizó el registro existente. */
    private String registradoPor;
    /** true si tipo=ESTUDIANTE + está en Anexo6A con estado que bloquea (whitelist SIMAT). */
    private boolean matriculadoSimat;
    /** Estado SIMAT actual del estudiante en Anexo6A si matriculadoSimat=true. */
    private String estadoSimat;
}
