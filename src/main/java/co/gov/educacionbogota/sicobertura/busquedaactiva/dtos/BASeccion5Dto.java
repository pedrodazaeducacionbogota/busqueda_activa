package co.gov.educacionbogota.sicobertura.busquedaactiva.dtos;

import lombok.Data;

/** Sección 5: acudiente. atiendeVisitaAcudiente=true reusa persona de sección 2. */
@Data
public class BASeccion5Dto {
    private boolean atiendeVisitaAcudiente;
    private Long idPersona;
    private String codigoTipoDocumento;
    private String numeroDocumento;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    /** Código parentesco ref_listado (descripcion=PARENTESCOS). */
    private String codigoParentesco;
    private String celulares;
    private String emails;
    /** Código nivel escolaridad ref_listado (descripcion=NIVELES_ESCOLARIDAD). */
    private String codigoNivelEscolaridad;
    /** Código ocupación ref_listado (descripcion=OCUPACIONES). */
    private String codigoOcupacion;
    private String familiarOtro;
}
