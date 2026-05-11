package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
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

    @Column(name = "pregunta_actual_id")
    private Integer preguntaActualId;

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
    private PersonaEntity estudiante;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solicitud_cupo_id", referencedColumnName = "id")
    private SolicitudEntity solicitudCupo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private PersonaEntity responsable;

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
    public Integer getPreguntaActualId() { return preguntaActualId; }
    public void setPreguntaActualId(Integer preguntaActualId) { this.preguntaActualId = preguntaActualId; }
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
    public PersonaEntity getEstudiante() { return estudiante; }
    public void setEstudiante(PersonaEntity estudiante) { this.estudiante = estudiante; }
    public SolicitudEntity getSolicitudCupo() { return solicitudCupo; }
    public void setSolicitudCupo(SolicitudEntity solicitudCupo) { this.solicitudCupo = solicitudCupo; }
    public PersonaEntity getResponsable() { return responsable; }
    public void setResponsable(PersonaEntity responsable) { this.responsable = responsable; }
    public List<FactorDesescolarizacion> getFactoresDesescolarizacion() { return factoresDesescolarizacion; }
    public void setFactoresDesescolarizacion(List<FactorDesescolarizacion> factoresDesescolarizacion) { this.factoresDesescolarizacion = factoresDesescolarizacion; }
}
