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
import javax.persistence.UniqueConstraint;

import co.gov.educacionbogota.sicobertura.entities.RefListado;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Normalización sección 4 del formulario BA. 1 fila por rango de edad declarado como
 * "no estudiando" en la visita. Reemplaza 24 columnas flat del legacy (4 cols × 6 rangos).
 *
 * UQ por (formulario, rango_edad_codigo): cada rango aparece 1 vez máximo por formulario.
 * Agregar nuevo rango edad SIN schema change → solo 1 fila más + entry en ref_listado.
 */
@Entity
@Table(name = "SC_DAT_BA_NO_ESTUDIANDO", uniqueConstraints = {
    @UniqueConstraint(name = "uk_ba_no_est_form_rango",
        columnNames = {"id_formulario", "rango_edad_codigo"})
})
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
     * Código rango edad SIMAT (ref_listado descripcion=RANGOS_EDADES.codigo):
     * "0-5", "6-12", "13-18", "19-28", "29-59", "60+".
     */
    @Column(name = "rango_edad_codigo", nullable = false, length = 10)
    private String rangoEdadCodigo;

    /** Cuántas personas no estudiando en este rango (0 si no aplica). */
    @Column(name = "cuantos", nullable = false)
    private int cuantos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_razon", referencedColumnName = "id_ref_listado")
    private RefListado razon;

    @Column(name = "razon_otra", length = 500)
    private String razonOtra;
}
