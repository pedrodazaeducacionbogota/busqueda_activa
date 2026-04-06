package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ba_estudiante")
public class EstudianteBusquedaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "es_mayor_edad")
    private Boolean esMayorEdad;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "pais_nacimiento", length = 50)
    private String paisNacimiento;

    @Column(name = "tipo_documento", length = 5)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20, unique = true)
    private String numeroDocumento;

    @Column(name = "primer_nombre", length = 50)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "sexo", length = 20)
    private String sexo;

    @Column(name = "etnia", length = 50)
    private String etnia;

    @Column(name = "cual_etnia", length = 100)
    private String cualEtnia;

    @Column(name = "tipo_discapacidad", length = 100)
    private String tipoDiscapacidad;

    @Column(name = "poblacion_diferencial", length = 50)
    private String poblacionDiferencial;

    @Column(name = "es_gestante")
    private Boolean esGestante;

    @Column(name = "direccion", length = 200)
    private String direccion;

    public EstudianteBusquedaActiva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Boolean getEsMayorEdad() { return esMayorEdad; }
    public void setEsMayorEdad(Boolean esMayorEdad) { this.esMayorEdad = esMayorEdad; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getPaisNacimiento() { return paisNacimiento; }
    public void setPaisNacimiento(String paisNacimiento) { this.paisNacimiento = paisNacimiento; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEtnia() { return etnia; }
    public void setEtnia(String etnia) { this.etnia = etnia; }
    public String getCualEtnia() { return cualEtnia; }
    public void setCualEtnia(String cualEtnia) { this.cualEtnia = cualEtnia; }
    public String getTipoDiscapacidad() { return tipoDiscapacidad; }
    public void setTipoDiscapacidad(String tipoDiscapacidad) { this.tipoDiscapacidad = tipoDiscapacidad; }
    public String getPoblacionDiferencial() { return poblacionDiferencial; }
    public void setPoblacionDiferencial(String poblacionDiferencial) { this.poblacionDiferencial = poblacionDiferencial; }
    public Boolean getEsGestante() { return esGestante; }
    public void setEsGestante(Boolean esGestante) { this.esGestante = esGestante; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
