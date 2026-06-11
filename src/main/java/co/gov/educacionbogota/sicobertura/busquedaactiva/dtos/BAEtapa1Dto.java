package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/**
 * Etapa 1 (HU-004): información sociodemográfica del estudiante.
 * Campos ref_listado llegan como id_ref_listado (Long) directo.
 */
@Data
public class BAEtapa1Dto {

    private boolean mayorEdadNombrePropio;
    private Long codigoPaisNacimiento;
    private Long codigoTipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    /** ISO-8601 yyyy-MM-dd o fecha completa con timezone. */
    private String fechaNacimiento;

    private Long codigoSexo;

    private Long codigoEtnia;
    private String etniaOtro;

    private boolean discapacidad;
    private Long codigoTipoDiscapacidad;
    private Boolean certDiscapacidad;
    private String soporteDiscapacidad;

    private Long codigoPoblacionDiferencial;
    private String poblacionOtro;

    /** Solo válidos si mayorEdadNombrePropio=true. */
    private String correo;
    private String celular;

    private Boolean gestante;

    /** Ubicación residencia. */
    private Long codigoLocalidad;
    private Long codigoBarrio;
    private String barrioOtro;

    /** Dirección estructurada. */
    private Long codigoTipoVia;
    private String numeroVia;
    private String letraVia;
    private String sufijoVia;
    private String numeroSecVia;
    private String numeroFinVia;
    private String direccion;
    private String direccionComplemento;
}
