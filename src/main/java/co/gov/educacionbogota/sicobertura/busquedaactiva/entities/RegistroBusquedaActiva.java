package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ba_registro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroBusquedaActiva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_registro", unique = true, nullable = false)
    private String numeroRegistro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_registro", nullable = false)
    private Date fechaRegistro;

    @Column(name = "ultima_etapa_registrada")
    private Integer ultimaEtapaRegistrada;

    @Column(name = "poblacion_atendida")
    private String poblacionAtendida;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "barrio")
    private String barrio;

    @Column(name = "estado", nullable = false)
    private String estado; // Pendiente, Finalizado

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estudiante_id", referencedColumnName = "id")
    private EstudianteBusquedaActiva estudiante;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "solicitud_cupo_id", referencedColumnName = "id")
    private SolicitudCupoBusquedaActiva solicitudCupo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private ResponsableBusquedaActiva responsable;
    
    // Asumiremos que FactorDesescolarizacion está integrado directamente o en relación 1:N
    //@OneToMany(mappedBy = "registro", cascade = CascadeType.ALL)
    //private List<FactorDesescolarizacion> factoresDesescolarizacion;
}
