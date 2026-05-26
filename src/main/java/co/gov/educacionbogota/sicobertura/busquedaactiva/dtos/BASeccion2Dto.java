package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/** Sección 2: persona que atiende la visita en sitio. */
@Data
public class BASeccion2Dto {
    private Long idPersona;
    /** Código tipo documento ref_listado (descripcion=TIPOS_DOCUMENTO). */
    private String codigoTipoDocumento;
    private String numeroDocumento;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String celulares;
    private String emails;
    /** Ubicación de residencia. */
    private String codigoLocalidad;
    private String codigoBarrio;
    private String barrioOtro;
    private String direccion;
    private String direccionComplemento;
    private Integer estrato;
    /** Código categoría sisbén ref_listado (descripcion=CATEGORIAS_SISBEN). */
    private String codigoCategoriaSisben;
}
