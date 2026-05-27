package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Etapa 4 (HU-007): factores de descolarización. Modelo agregado por rango.
 *
 * HU-007 paso 1: ¿en el núcleo familiar hay NNAJ no estudiando? Si {@code existenNoEstudiando=false}
 * la lista va vacía y la etapa solo marca finalizado.
 */
@Data
public class BAEtapa4Dto {

    /** ¿Existen NNAJ no estudiando en el núcleo familiar? */
    private boolean existenNoEstudiando;

    @Valid
    private List<NoEstudiandoItem> rangos = new ArrayList<>();

    @Data
    public static class NoEstudiandoItem {
        /** Código rango edad (descripcion=RANGOS_EDADES_BA): 0_Y_5_ANOS, 6_Y_10_ANOS, 11_Y_15_ANOS, MAYOR_A_15_ANOS. */
        @NotBlank
        private String rangoEdadCodigo;

        @NotNull
        private Integer cuantos;

        /** Código razón principal (descripcion=RAZONES_NOESCOLAR_BA). */
        private String codigoRazon;

        /** Código sub-razón cuando codigoRazon=OTROS_CUALES (descripcion=RAZONES_NOESCOLAR_OTRAS_BA). */
        private String codigoRazonOtra;
    }
}
