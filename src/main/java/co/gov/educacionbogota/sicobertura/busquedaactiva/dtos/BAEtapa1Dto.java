package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/**
 * Etapa 1 (HU-004): información sociodemográfica del estudiante.
 * Códigos refieren ref_listado por (codigo, descripcion).
 */
@Data
public class BAEtapa1Dto {

    /** ¿Estudiante mayor de edad y se representa a nombre propio? Habilita correo/celular. */
    private boolean mayorEdadNombrePropio;

    /** Código país nacimiento (descripcion=PAIS). */
    private String codigoPaisNacimiento;

    /** Código tipo documento (descripcion=TIPOS_DOCUMENTO). */
    private String codigoTipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    /** Formato yyyy-MM-dd. */
    private String fechaNacimiento;

    /** Código sexo (descripcion=SEXOS). */
    private String codigoSexo;

    /** Código etnia (descripcion=ETNIAS). Si OTRO → etniaOtro. */
    private String codigoEtnia;
    private String etniaOtro;

    /** ¿Tiene discapacidad/talento/trastorno? */
    private boolean discapacidad;
    /** Código tipo discapacidad (descripcion=TIPOS_DISCAPACIDAD). Solo si discapacidad=true. */
    private String codigoTipoDiscapacidad;
    /** ¿Cuenta con certificado o diagnóstico? */
    private boolean certDiscapacidad;
    /** Ruta/referencia del soporte PDF de discapacidad. */
    private String soporteDiscapacidad;

    /** Código población diferencial (descripcion=POBLACION_EVENT_BA). Si OTRO → poblacionOtro. */
    private String codigoPoblacionDiferencial;
    private String poblacionOtro;

    /** Solo habilitados si mayorEdadNombrePropio=true. */
    private String correo;
    private String celular;

    /** Ubicación de residencia. */
    private String codigoLocalidad;
    private String codigoBarrio;
    private String barrioOtro;

    /** ¿Es gestante? */
    private boolean gestante;

    /** Dirección estructurada. Código tipo vía (descripcion=TIPOS_VIA) + dirección armada + complemento. */
    private String codigoTipoVia;
    private String direccion;
    private String direccionComplemento;
}
