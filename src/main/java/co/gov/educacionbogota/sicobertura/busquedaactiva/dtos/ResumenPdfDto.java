package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** PDF resumen formulario BA. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPdfDto {
    private boolean estado;
    private Long idSolicitud;
    /** PDF bytes encoded base64. */
    private String base64;
}
