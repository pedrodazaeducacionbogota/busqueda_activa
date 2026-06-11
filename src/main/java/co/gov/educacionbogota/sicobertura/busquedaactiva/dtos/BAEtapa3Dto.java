package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/**
 * Etapa 3 (HU-006): información del responsable/acudiente.
 * Campos ref_listado llegan como id_ref_listado (Long) directo.
 */
@Data
public class BAEtapa3Dto {

    private Long codigoTipoDocumento;
    private String numeroDocumento;

    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private String correo;
    private String celular;

    private Long codigoParentesco;
    private String parentescoOtro;

    private Long codigoNivelEscolaridad;
    private Long codigoOcupacion;

    private Long codigoLocalidad;
    private Long codigoBarrio;
    private String barrioOtro;

    private Long codigoTipoVia;
    private String numeroVia;
    private String letraVia;
    private String sufijoVia;
    private String numeroSecVia;
    private String numeroFinVia;
    private String direccion;
    private String direccionComplemento;
}
