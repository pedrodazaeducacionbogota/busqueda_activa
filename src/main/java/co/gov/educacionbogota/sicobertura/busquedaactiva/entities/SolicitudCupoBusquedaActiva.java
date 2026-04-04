package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ba_solicitud_cupo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
