package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;

@Entity
@Table(name = "ba_responsable")
public class ResponsableBusquedaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_documento", length = 5, nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20, nullable = false)
    private String numeroDocumento;

    @Column(name = "primer_nombre", length = 50, nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "celular", length = 15, nullable = false)
    private String celular;

    @Column(name = "parentesco", length = 50, nullable = false)
    private String parentesco;

    @Column(name = "nivel_escolaridad", length = 50, nullable = false)
    private String nivelEscolaridad;

    @Column(name = "ocupacion", length = 50, nullable = false)
    private String ocupacion;

    @Column(name = "localidad", length = 50, nullable = false)
    private String localidad;

    @Column(name = "barrio", length = 50, nullable = false)
    private String barrio;

    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;

    public ResponsableBusquedaActiva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
    public String getNivelEscolaridad() { return nivelEscolaridad; }
    public void setNivelEscolaridad(String nivelEscolaridad) { this.nivelEscolaridad = nivelEscolaridad; }
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
