package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import co.gov.educacionbogota.sicobertura.audit.Auditable;
import co.gov.educacionbogota.sicobertura.entities.EstudianteEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SedeEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.entities.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Formulario Búsqueda Activa. Root del wizard 7-secciones.
 * Refactor del legacy `BUSQUEDA_ACTIVA_DAT` 2024: sección 4 (no estudiando por rango) normalizada
 * en {@link BANoEstudiandoEntity}. Resto de secciones en columnas flat por simplicidad.
 *
 * Lifecycle: POST crea root vacío → PUT seccion{1..7} incrementa `ultima_seccion`.
 * Sección 7 marca `finalizado=true`.
 */
@Entity
@Table(name = "SC_DAT_BA_FORMULARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Auditable(table = "SC_DAT_BA_FORMULARIO")
public class BusquedaActivaFormularioEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vigencia", nullable = false)
    private Integer vigencia;

    @Column(name = "etapa", nullable = false)
    private Integer etapa;

    @Column(name = "fecha_crea", nullable = false, updatable = false)
    private LocalDateTime fechaCrea;

    @Column(name = "finalizado", nullable = false)
    private boolean finalizado = false;

    /** Tracking wizard. 0=creado vacío, 1..7=última sección actualizada. */
    @Column(name = "ultima_seccion", nullable = false)
    private int ultimaSeccion = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesional", nullable = false)
    private Usuario profesional;

    // -------- Sección 1: actividad + ubicación visita --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", referencedColumnName = "id_ref_listado")
    private RefListado actividad;

    @Column(name = "actividad_otra", length = 255)
    private String actividadOtra;

    @Column(name = "nombre_evento_feria", length = 255)
    private String nombreEventoFeria;

    /** CSV de poblaciones (multiselect front). */
    @Column(name = "poblacion_evento", length = 500)
    private String poblacionEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;

    // -------- Sección 2: atiende visita (persona en sitio) --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atiende_visita")
    private PersonaEntity atiendeVisita;

    // -------- Sección 3: colegios/jardines cerca --------
    @Column(name = "colegios_cerca")
    private boolean colegiosCerca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ide_colegio_cerca")
    private SedeEntity ideColegioCerca;

    @Column(name = "colegio_cerca_cual", length = 255)
    private String colegioCercaCual;

    @Column(name = "jardines_cerca")
    private boolean jardinesCerca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ide_jardin_cerca")
    private SedeEntity ideJardinCerca;

    @Column(name = "jardin_cerca_cual", length = 255)
    private String jardinCercaCual;

    // -------- Sección 4: no estudiando por rango edad → tabla normalizada --------
    @OneToMany(mappedBy = "formulario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BANoEstudiandoEntity> noEstudiandoRangos = new ArrayList<>();

    // -------- Sección 5: acudiente --------
    @Column(name = "atiende_visita_acudiente")
    private boolean atiendeVisitaAcudiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acudiente")
    private PersonaEntity acudiente;

    // -------- Sección 6: estudiante --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante")
    private EstudianteEntity estudiante;

    // -------- Sección 7: educativo + solicitud cupo --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ult_anio_estudio", referencedColumnName = "id_ref_listado")
    private RefListado ultimoAnioEstudio;

    @Column(name = "repitio_ultimo_anio")
    private boolean repitioUltimoAnio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veces_repitio", referencedColumnName = "id_ref_listado")
    private RefListado vecesRepitioAnio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ult_anio_aprobado", referencedColumnName = "id_ref_listado")
    private RefListado ultimoAnioAprobado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ide_solicita_cupo")
    private SedeEntity ideSolicitaCupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grado_solicita_cupo", referencedColumnName = "id_ref_listado")
    private RefListado gradoSolicitaCupo;

    /** Solicitud derivada al finalizar (sec 7) — opcional. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formulario_solicitud")
    private SolicitudEntity formularioSolicitud;

    @PrePersist
    protected void onCreate() {
        if (fechaCrea == null) {
            fechaCrea = LocalDateTime.now();
        }
    }

    public void agregarNoEstudiando(BANoEstudiandoEntity item) {
        item.setFormulario(this);
        this.noEstudiandoRangos.add(item);
    }
}
