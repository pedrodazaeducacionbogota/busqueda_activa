package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response del check dedup por documento+etapa+vigencia. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckEstudianteDto {
    /** true = no existe formulario previo, puede iniciar wizard. */
    private boolean nuevo;
    /** Si nuevo=false, id del formulario existente para retomar wizard. */
    private Long idSolicitud;
}
