package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila resumen listado formularios BA para tabla front. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BAInfoTablaDto {
    private Long id;
    private String fechaCrea;
    private boolean finalizado;
    private int ultimaSeccion;
    private String actividad;
    private String nombreEventoFeria;
    private String poblacion;
    private String localidad;
    private String barrio;
    private String atiendeVisita;
    private String estudiante;
}
