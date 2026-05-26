package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Lista grados elegibles según edad+1 del estudiante (clamp 3..18, ≥18→99). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradosAprobadosDto {
    private int edadConsulta;
    private List<RefListadoLiteDto> grados;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefListadoLiteDto {
        private Long id;
        private String codigo;
        private String descripcion;
    }
}
