package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/** Sección 6: datos personales del estudiante. */
@Data
public class BASeccion6Dto {
    private Long idPersona;
    private String codigoTipoDocumento;
    private String numeroDocumento;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    /** Código municipio expedición doc (ref_listado descripcion=MUNICIPIOS). Si paisNacimiento=COL. */
    private String municipioExpDoc;
    /** Código país nacimiento ref_listado (descripcion=PAISES). */
    private String paisNacimiento;
    /** Formato yyyy-MM-dd. */
    private String fechaNacimiento;
    private String codigoSexo;
    private String codigoGenero;
    private boolean discapacidad;
    private String codigoTipoDiscapacidad;
    private boolean certDiscapacidad;
    /** Código enfoque diferencial ref_listado (descripcion=ENFOQUES_DIFERENCIAL). Opcional. */
    private String codigoEnfoqueDiferencial;
}
