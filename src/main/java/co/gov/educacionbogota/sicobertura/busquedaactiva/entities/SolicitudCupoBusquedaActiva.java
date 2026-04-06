package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;

@Entity
@Table(name = "ba_solicitud_cupo")
public class SolicitudCupoBusquedaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ultimo_ano_aprobado", length = 30)
    private String ultimoAnoAprobado;

    @Column(name = "localidad_institucion", length = 50)
    private String localidadInstitucion;

    @Column(name = "instituciones_educativas", length = 500)
    private String institucionesEducativas; // comma separated list

    @Column(name = "grado_asignado", length = 30)
    private String gradoAsignado;

    @Column(name = "tiene_hermanos", nullable = false)
    private Boolean tieneHermanos;

    @Column(name = "tipo_documento_hermano", length = 5)
    private String tipoDocumentoHermano;

    @Column(name = "numero_documento_hermano", length = 20)
    private String numeroDocumentoHermano;

    @Column(name = "primer_nombre_hermano", length = 50)
    private String primerNombreHermano;

    @Column(name = "segundo_nombre_hermano", length = 50)
    private String segundoNombreHermano;

    @Column(name = "primer_apellido_hermano", length = 50)
    private String primerApellidoHermano;

    @Column(name = "segundo_apellido_hermano", length = 50)
    private String segundoApellidoHermano;

    @Column(name = "misma_institucion")
    private Boolean mismaInstitucion;

    @Column(name = "institucion_hermano", length = 150)
    private String institucionHermano;

    public SolicitudCupoBusquedaActiva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUltimoAnoAprobado() { return ultimoAnoAprobado; }
    public void setUltimoAnoAprobado(String ultimoAnoAprobado) { this.ultimoAnoAprobado = ultimoAnoAprobado; }
    public String getLocalidadInstitucion() { return localidadInstitucion; }
    public void setLocalidadInstitucion(String localidadInstitucion) { this.localidadInstitucion = localidadInstitucion; }
    public String getInstitucionesEducativas() { return institucionesEducativas; }
    public void setInstitucionesEducativas(String institucionesEducativas) { this.institucionesEducativas = institucionesEducativas; }
    public String getGradoAsignado() { return gradoAsignado; }
    public void setGradoAsignado(String gradoAsignado) { this.gradoAsignado = gradoAsignado; }
    public Boolean getTieneHermanos() { return tieneHermanos; }
    public void setTieneHermanos(Boolean tieneHermanos) { this.tieneHermanos = tieneHermanos; }
    public String getTipoDocumentoHermano() { return tipoDocumentoHermano; }
    public void setTipoDocumentoHermano(String tipoDocumentoHermano) { this.tipoDocumentoHermano = tipoDocumentoHermano; }
    public String getNumeroDocumentoHermano() { return numeroDocumentoHermano; }
    public void setNumeroDocumentoHermano(String numeroDocumentoHermano) { this.numeroDocumentoHermano = numeroDocumentoHermano; }
    public String getPrimerNombreHermano() { return primerNombreHermano; }
    public void setPrimerNombreHermano(String primerNombreHermano) { this.primerNombreHermano = primerNombreHermano; }
    public String getSegundoNombreHermano() { return segundoNombreHermano; }
    public void setSegundoNombreHermano(String segundoNombreHermano) { this.segundoNombreHermano = segundoNombreHermano; }
    public String getPrimerApellidoHermano() { return primerApellidoHermano; }
    public void setPrimerApellidoHermano(String primerApellidoHermano) { this.primerApellidoHermano = primerApellidoHermano; }
    public String getSegundoApellidoHermano() { return segundoApellidoHermano; }
    public void setSegundoApellidoHermano(String segundoApellidoHermano) { this.segundoApellidoHermano = segundoApellidoHermano; }
    public Boolean getMismaInstitucion() { return mismaInstitucion; }
    public void setMismaInstitucion(Boolean mismaInstitucion) { this.mismaInstitucion = mismaInstitucion; }
    public String getInstitucionHermano() { return institucionHermano; }
    public void setInstitucionHermano(String institucionHermano) { this.institucionHermano = institucionHermano; }
}
