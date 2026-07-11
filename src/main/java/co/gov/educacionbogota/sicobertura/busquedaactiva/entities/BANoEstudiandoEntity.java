package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import co.gov.educacionbogota.sicobertura.entities.RefListado;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Etapa 4 (HU-007) factores descolarización. Modelo por niño: 1 fila por niño no estudiando.
 *
 * Cambio 2026-07-10: cada niño lleva su propia razón para no perder info de BA. Removida UK
 * por (formulario, rango_edad_codigo) — el mismo rango puede aparecer N veces si hay N niños
 * con distintas razones dentro del núcleo familiar.
 */
@Entity
@Table(name = "SC_DAT_BA_NO_ESTUDIANDO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BANoEstudiandoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formulario", nullable = false)
    private BusquedaActivaFormularioEntity formulario;

    /**
     * Código rango edad (ref_listado descripcion=RANGOS_EDADES_BA.codigo):
     * "0_Y_5_ANOS", "6_Y_10_ANOS", "11_Y_15_ANOS", "MAYOR_A_15_ANOS".
     */
    @Column(name = "rango_edad_codigo", nullable = false, length = 20)
    private String rangoEdadCodigo;

    /** Cuántas personas no estudiando en este rango (>=1). */
    @Column(name = "cuantos", nullable = false)
    private int cuantos;

    /** Razón principal (ref_listado descripcion=RAZONES_NOESCOLAR_BA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_razon", referencedColumnName = "id_ref_listado")
    private RefListado razon;

    /** Sub-razón cuando razon=OTROS_CUALES (ref_listado descripcion=RAZONES_NOESCOLAR_OTRAS_BA). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_razon_otra", referencedColumnName = "id_ref_listado")
    private RefListado razonOtra;
}
