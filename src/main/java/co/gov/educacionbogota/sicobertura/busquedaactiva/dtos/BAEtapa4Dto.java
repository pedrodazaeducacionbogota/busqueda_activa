package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Etapa 4 (HU-007): factores de descolarización.
 * Campos ref_listado llegan como id_ref_listado (Long) directo.
 */
@Data
public class BAEtapa4Dto {

    private boolean existenNoEstudiando;

    @Valid
    private List<NoEstudiandoItem> rangos = new ArrayList<>();

    @Data
    public static class NoEstudiandoItem {
        /** ID ref_listado del rango edad (descripcion=RANGOS_EDADES_BA). */
        @NotNull
        private Long codigoRangoEdad;

        @NotNull
        private Integer cuantos;

        private Long codigoRazon;

        private Long codigoRazonOtra;
    }
}
