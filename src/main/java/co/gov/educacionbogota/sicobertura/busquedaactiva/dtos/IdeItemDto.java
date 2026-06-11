package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sede de institución educativa para selección en wizard BA.")
public class IdeItemDto {

    @Schema(description = "ID interno de la sede.", example = "42")
    private Long id;

    @Schema(description = "Nombre de la sede.", example = "Sede Principal")
    private String nombreSede;

    @Schema(description = "Nombre del colegio (IDE).", example = "Colegio Mayor de San Bartolomé")
    private String nombreColegio;

    @Schema(description = "Código DANE de la sede.", example = "11001001234")
    private String codigoDane;

    @Schema(description = "Localidad asignada al colegio.", example = "Usaquén")
    private String direccion;

    @Schema(description = "Longitud geográfica de la localidad (aux1).", example = "-74.0699969")
    private String longitud;

    @Schema(description = "Latitud geográfica de la localidad (aux2).", example = "4.7448306")
    private String latitud;
}
