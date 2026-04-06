package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ba_registro")
public class RegistroBusquedaActiva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_registro", unique = true, nullable = false)
    private String numeroRegistro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_registro", nullable = false)
    private Date fechaRegistro;

    @Column(name = "ultima_etapa_registrada")
    private Integer ultimaEtapaRegistrada;

    @Column(name = "poblacion_atendida")
    private String poblacionAtendida;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "barrio")
    private String barrio;

    @Column(name = "estado", nullable = false)
    private String estado; // Pendiente, Finalizado

    @Column(name = "tiene_ninos_sin_estudio")
    private Boolean tieneNinosSinEstudio;

    @Column(name = "cantidad_ninos_sin_estudio")
    private Integer cantidadNinosSinEstudio;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estudiante_id", referencedColumnName = "id")
    private EstudianteBusquedaActiva estudiante;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solicitud_cupo_id", referencedColumnName = "id")
    private SolicitudCupoBusquedaActiva solicitudCupo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private ResponsableBusquedaActiva responsable;

    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FactorDesescolarizacion> factoresDesescolarizacion = new ArrayList<>();

    public RegistroBusquedaActiva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroRegistro() { return numeroRegistro; }
    public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Integer getUltimaEtapaRegistrada() { return ultimaEtapaRegistrada; }
    public void setUltimaEtapaRegistrada(Integer ultimaEtapaRegistrada) { this.ultimaEtapaRegistrada = ultimaEtapaRegistrada; }
    public String getPoblacionAtendida() { return poblacionAtendida; }
    public void setPoblacionAtendida(String poblacionAtendida) { this.poblacionAtendida = poblacionAtendida; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Boolean getTieneNinosSinEstudio() { return tieneNinosSinEstudio; }
    public void setTieneNinosSinEstudio(Boolean tieneNinosSinEstudio) { this.tieneNinosSinEstudio = tieneNinosSinEstudio; }
    public Integer getCantidadNinosSinEstudio() { return cantidadNinosSinEstudio; }
    public void setCantidadNinosSinEstudio(Integer cantidadNinosSinEstudio) { this.cantidadNinosSinEstudio = cantidadNinosSinEstudio; }
    public EstudianteBusquedaActiva getEstudiante() { return estudiante; }
    public void setEstudiante(EstudianteBusquedaActiva estudiante) { this.estudiante = estudiante; }
    public SolicitudCupoBusquedaActiva getSolicitudCupo() { return solicitudCupo; }
    public void setSolicitudCupo(SolicitudCupoBusquedaActiva solicitudCupo) { this.solicitudCupo = solicitudCupo; }
    public ResponsableBusquedaActiva getResponsable() { return responsable; }
    public void setResponsable(ResponsableBusquedaActiva responsable) { this.responsable = responsable; }
    public List<FactorDesescolarizacion> getFactoresDesescolarizacion() { return factoresDesescolarizacion; }
    public void setFactoresDesescolarizacion(List<FactorDesescolarizacion> factoresDesescolarizacion) { this.factoresDesescolarizacion = factoresDesescolarizacion; }
}
