package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ba_factor_desescolarizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactorDesescolarizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "motivo", length = 150, nullable = false)
    private String motivo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_id", nullable = false)
    private RegistroBusquedaActiva registro;
}
