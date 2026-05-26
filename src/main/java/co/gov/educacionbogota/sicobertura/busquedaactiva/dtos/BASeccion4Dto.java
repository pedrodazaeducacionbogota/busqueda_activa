package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/** Sección 4: personas no estudiando por rango edad. Normalizada vs legacy (24 cols flat). */
@Data
public class BASeccion4Dto {

    @NotEmpty
    @Valid
    private List<NoEstudiandoItem> rangos;

    @Data
    public static class NoEstudiandoItem {
        /** "0-5", "6-12", "13-18", "19-28", "29-59", "60+". */
        @NotBlank
        private String rangoEdadCodigo;

        @NotNull
        private Integer cuantos;

        /** Código razón ref_listado (descripcion=RAZONES_NOESCOLAR_BA). null si cuantos=0. */
        private String codigoRazon;

        private String razonOtra;
    }
}
