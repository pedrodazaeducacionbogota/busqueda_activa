package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import co.gov.educacionbogota.sicobertura.entities.IdeEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;

import javax.persistence.*;

@Entity
@Table(name = "ba_caracterizacion")
public class CaracterizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", referencedColumnName = "id", nullable = false)
    private SolicitudEntity solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localidad_institucion", referencedColumnName = "id_ref_listado")
    private RefListado localidadInstitucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion", referencedColumnName = "id")
    private IdeEntity institucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion_hermano", referencedColumnName = "id")
    private IdeEntity institucionHermano;

    @Column(name = "misma_institucion")
    private Boolean mismaInstitucion;

    public CaracterizacionEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SolicitudEntity getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudEntity solicitud) { this.solicitud = solicitud; }

    public RefListado getLocalidadInstitucion() { return localidadInstitucion; }
    public void setLocalidadInstitucion(RefListado localidadInstitucion) { this.localidadInstitucion = localidadInstitucion; }

    public IdeEntity getInstitucion() { return institucion; }
    public void setInstitucion(IdeEntity institucion) { this.institucion = institucion; }

    public IdeEntity getInstitucionHermano() { return institucionHermano; }
    public void setInstitucionHermano(IdeEntity institucionHermano) { this.institucionHermano = institucionHermano; }

    public Boolean getMismaInstitucion() { return mismaInstitucion; }
    public void setMismaInstitucion(Boolean mismaInstitucion) { this.mismaInstitucion = mismaInstitucion; }
}
