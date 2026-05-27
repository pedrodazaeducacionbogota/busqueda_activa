package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila resumen listado formularios BA para tarjetas front (HU-003). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BAInfoTablaDto {
    private Long id;
    private String fechaCrea;
    private boolean finalizado;
    /** Última etapa registrada (0-4). */
    private int ultimaEtapa;
    /** "Finalizado" / "Pendiente". */
    private String estado;
    /** Población diferencial del estudiante. */
    private String poblacion;
    private String localidad;
    private String barrio;
    /** Nombre del estudiante. */
    private String estudiante;
}
