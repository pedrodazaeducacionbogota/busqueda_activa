package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import lombok.Data;

/**
 * Etapa 1 (HU-004): información sociodemográfica del estudiante.
 * Los campos ref_listado llegan como RefListadoKVDto {id, codigo, valorTxt, valorInt}.
 */
@Data
public class BAEtapa1Dto {

    /** ¿Estudiante mayor de edad y se representa a nombre propio? Habilita correo/celular. */
    private boolean mayorEdadNombrePropio;

    /** País de nacimiento (descripcion=PAIS). */
    private RefListadoKVDto paisNacimiento;

    /** Tipo de documento (descripcion=TIPOS_DOCUMENTO). */
    private RefListadoKVDto tipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    /** Formato yyyy-MM-dd. */
    private String fechaNacimiento;

    /** Sexo (descripcion=SEXOS). */
    private RefListadoKVDto sexo;

    /** Etnia (descripcion=ETNIAS). Si OTRO → etniaOtro. */
    private RefListadoKVDto etnia;
    private String etniaOtro;

    /** ¿Tiene discapacidad/talento/trastorno? */
    private boolean discapacidad;
    /** Tipo discapacidad (descripcion=TIPOS_DISCAPACIDAD). Solo si discapacidad=true. */
    private RefListadoKVDto tipoDiscapacidad;
    /** ¿Cuenta con certificado o diagnóstico? */
    private boolean certDiscapacidad;
    /** Ruta/referencia del soporte PDF de discapacidad. */
    private String soporteDiscapacidad;

    /** Población diferencial (descripcion=POBLACION_EVENT_BA). Si OTRO → poblacionOtro. */
    private RefListadoKVDto poblacionDiferencial;
    private String poblacionOtro;

    /** Solo habilitados si mayorEdadNombrePropio=true. */
    private String correo;
    private String celular;

    /** Ubicación de residencia. */
    private RefListadoKVDto localidad;
    private RefListadoKVDto barrio;
    private String barrioOtro;

    /** ¿Es gestante? */
    private boolean gestante;

    /** Tipo de vía (descripcion=TIPOS_VIA) + dirección armada + complemento. */
    private RefListadoKVDto tipoVia;
    private String direccion;
    private String direccionComplemento;
}
