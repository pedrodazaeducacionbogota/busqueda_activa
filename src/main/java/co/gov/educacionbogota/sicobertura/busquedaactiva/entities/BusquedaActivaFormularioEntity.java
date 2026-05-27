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
import co.gov.educacionbogota.sicobertura.entities.IdeEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Formulario Búsqueda Activa. Root del wizard 4-etapas (HU 12-IF-049, validado funcional 2026).
 *
 * Realineación 2026-05-27 del modelo 7-secciones legacy: el HU define 4 etapas y reutiliza
 * las entidades transversales del módulo inscripciones (SolicitudEntity + SolicitudColegio).
 *
 * <ul>
 *   <li>Etapa 1 (HU-004): estudiante sociodemográfico → {@link EstudianteEntity}</li>
 *   <li>Etapa 2 (HU-005): solicitud cupo + hermanos + hasta 10 IE → {@link SolicitudEntity}</li>
 *   <li>Etapa 3 (HU-006): responsable/acudiente → {@link PersonaEntity}</li>
 *   <li>Etapa 4 (HU-007): factores descolarización → {@link BANoEstudiandoEntity} (agregado/rango)</li>
 * </ul>
 *
 * Lifecycle: POST crea root vacío → PUT etapa{1..4} incrementa `ultima_etapa`.
 * Etapa 4 marca `finalizado=true`.
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

    /** Tracking wizard. 0=creado vacío, 1..4=última etapa actualizada. */
    @Column(name = "ultima_etapa", nullable = false)
    private int ultimaEtapa = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesional", nullable = false)
    private Usuario profesional;

    // -------- Etapa 1: estudiante sociodemográfico (HU-004) --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante")
    private EstudianteEntity estudiante;

    // -------- Etapa 2: solicitud cupo + hermanos + colegios (HU-005) --------
    /** Solicitud cupo standalone BA (reutiliza SOLICITUDES_DAT + SOLICITUD_COLEGIOS_DAT, no dispara inscripción). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud")
    private SolicitudEntity solicitud;

    /** ¿Desea misma institución que el hermano? (HU-005 paso 12). */
    @Column(name = "misma_institucion_hermano")
    private boolean mismaInstitucionHermano;

    /** Institución donde está el hermano (HU-005 paso 13). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion_hermano")
    private IdeEntity institucionHermano;

    // -------- Etapa 3: responsable/acudiente (HU-006) --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acudiente")
    private PersonaEntity acudiente;

    // -------- Etapa 4: factores descolarización (HU-007) → tabla normalizada agregada/rango --------
    @OneToMany(mappedBy = "formulario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BANoEstudiandoEntity> noEstudiandoRangos = new ArrayList<>();

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
