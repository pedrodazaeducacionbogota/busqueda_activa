package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import java.util.ArrayList;
import java.util.List;

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
    /** "Finalizado" / "Pendiente" / "En proceso". */
    private String estado;
    /** Población diferencial del estudiante. */
    private String poblacion;
    private String localidad;
    private String barrio;
    /** Nombre del estudiante. */
    private String estudiante;
    /** Tipo documento estudiante (id ref_listado TIPOS_DOCUMENTO). */
    private Long tipoDocumentoEstudiante;
    /** Numero documento estudiante. */
    private String numeroDocumentoEstudiante;
    /** Lista etapas diligenciadas (1..4). Vacia si ninguna. */
    private List<Integer> etapasDiligenciadas = new ArrayList<>();
}
