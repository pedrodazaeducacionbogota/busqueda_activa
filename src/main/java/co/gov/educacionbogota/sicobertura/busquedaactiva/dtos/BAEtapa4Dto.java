package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Etapa 4 (HU-007): factores de descolarización.
 * Los campos ref_listado llegan como RefListadoKVDto {id, codigo, valorTxt, valorInt}.
 */
@Data
public class BAEtapa4Dto {

    /** ¿Existen NNAJ no estudiando en el núcleo familiar? */
    private boolean existenNoEstudiando;

    @Valid
    private List<NoEstudiandoItem> rangos = new ArrayList<>();

    @Data
    public static class NoEstudiandoItem {
        /** Código rango edad (descripcion=RANGOS_EDADES_BA): 0_Y_5_ANOS, 6_Y_10_ANOS, etc. */
        @NotBlank
        private String rangoEdadCodigo;

        @NotNull
        private Integer cuantos;

        /** Razón principal (descripcion=RAZONES_NOESCOLAR_BA). */
        private RefListadoKVDto razon;

        /** Sub-razón cuando razon.codigo=OTROS_CUALES (descripcion=RAZONES_NOESCOLAR_OTRAS_BA). */
        private RefListadoKVDto razonOtra;
    }
}
