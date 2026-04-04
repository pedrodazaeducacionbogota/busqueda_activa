package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ba_estudiante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteBusquedaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "es_mayor_edad", nullable = false)
    private Boolean esMayorEdad;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "pais_nacimiento", length = 50, nullable = false)
    private String paisNacimiento;

    @Column(name = "tipo_documento", length = 5, nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20, nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "primer_nombre", length = 50, nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", length = 50, nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private Date fechaNacimiento;

    @Column(name = "sexo", length = 20, nullable = false)
    private String sexo;

    @Column(name = "etnia", length = 50, nullable = false)
    private String etnia;

    @Column(name = "cual_etnia", length = 100)
    private String cualEtnia;

    @Column(name = "tipo_discapacidad", length = 100)
    private String tipoDiscapacidad;

    @Column(name = "poblacion_diferencial", length = 50, nullable = false)
    private String poblacionDiferencial;

    @Column(name = "es_gestante")
    private Boolean esGestante;

    @Column(name = "direccion", length = 200, nullable = false)
    private String direccion;
}
