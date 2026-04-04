package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ba_responsable")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsableBusquedaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_documento", length = 5, nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20, nullable = false)
    private String numeroDocumento;

    @Column(name = "primer_nombre", length = 50, nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "celular", length = 15, nullable = false)
    private String celular;

    @Column(name = "parentesco", length = 50, nullable = false)
    private String parentesco;

    @Column(name = "nivel_escolaridad", length = 50, nullable = false)
    private String nivelEscolaridad;

    @Column(name = "ocupacion", length = 50, nullable = false)
    private String ocupacion;

    @Column(name = "localidad", length = 50, nullable = false)
    private String localidad;

    @Column(name = "barrio", length = 50, nullable = false)
    private String barrio;

    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;
}
