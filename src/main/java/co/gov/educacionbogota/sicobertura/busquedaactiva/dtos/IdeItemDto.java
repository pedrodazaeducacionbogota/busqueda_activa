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

    @Schema(description = "Dirección física de la sede (IDECA).", example = "KR 18 A # 187 - 67/65")
    private String direccion;

    @Schema(description = "Longitud geográfica de la sede (IDECA).", example = "-74.03917132")
    private String longitud;

    @Schema(description = "Latitud geográfica de la sede (IDECA).", example = "4.765726349")
    private String latitud;
}
