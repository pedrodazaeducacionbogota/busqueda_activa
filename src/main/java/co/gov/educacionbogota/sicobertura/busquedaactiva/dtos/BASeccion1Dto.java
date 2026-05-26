package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.List;
import lombok.Data;

/** Sección 1: actividad + ubicación visita. */
@Data
public class BASeccion1Dto {
    /** Código actividad en ref_listado (descripcion=ACTIVIDADES_BA). */
    private String codigoActividad;
    private String actividadOtra;
    private String nombreEventoFeria;
    /** Multiselect front; back guarda CSV. */
    private List<String> poblacionEvento;
    /** Código localidad ref_listado (descripcion=LOCALIDADES). */
    private String codigoLocalidad;
    /** Código barrio ref_listado (descripcion=BARRIOS). "0" o vacío → usa barrioOtro. */
    private String codigoBarrio;
    private String barrioOtro;
}
