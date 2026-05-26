package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response estándar POST formulario + PUT secciones. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBASeccionesDto {
    private Long idFormulario;
    private Long idProfesional;
}
