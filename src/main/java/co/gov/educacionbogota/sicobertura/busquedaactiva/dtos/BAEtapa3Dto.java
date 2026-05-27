package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/**
 * Etapa 3 (HU-006): información de contacto del responsable o acudiente.
 */
@Data
public class BAEtapa3Dto {

    /** Código tipo documento (descripcion=TIPOS_DOCUMENTO). */
    private String codigoTipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private String correo;
    private String celular;

    /** Código parentesco (descripcion=PARENTESCOS). Si OTRO → parentescoOtro. */
    private String codigoParentesco;
    private String parentescoOtro;

    /** Código nivel escolaridad (descripcion=NIVELES_ESCOLARIDAD). */
    private String codigoNivelEscolaridad;

    /** Código ocupación (descripcion=OCUPACIONES). */
    private String codigoOcupacion;

    /** Ubicación de residencia. */
    private String codigoLocalidad;
    private String codigoBarrio;
    private String barrioOtro;

    /** Dirección estructurada. */
    private String codigoTipoVia;
    private String direccion;
    private String direccionComplemento;
}
