package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import lombok.Data;

/**
 * Etapa 3 (HU-006): información de contacto del responsable o acudiente.
 * Los campos ref_listado llegan como RefListadoKVDto {id, codigo, valorTxt, valorInt}.
 */
@Data
public class BAEtapa3Dto {

    /** Tipo de documento (descripcion=TIPOS_DOCUMENTO). */
    private RefListadoKVDto tipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private String correo;
    private String celular;

    /** Parentesco (descripcion=PARENTESCOS). Si OTRO → parentescoOtro. */
    private RefListadoKVDto parentesco;
    private String parentescoOtro;

    /** Nivel de escolaridad (descripcion=NIVELES_ESCOLARIDAD). */
    private RefListadoKVDto nivelEscolaridad;

    /** Ocupación (descripcion=OCUPACIONES). */
    private RefListadoKVDto ocupacion;

    /** Ubicación de residencia. */
    private RefListadoKVDto localidad;
    private RefListadoKVDto barrio;
    private String barrioOtro;

    /** Dirección estructurada. */
    private RefListadoKVDto tipoVia;
    private String direccion;
    private String direccionComplemento;
}
