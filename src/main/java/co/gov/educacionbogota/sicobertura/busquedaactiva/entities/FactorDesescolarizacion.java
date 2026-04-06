package co.gov.educacionbogota.sicobertura.busquedaactiva.entities;

import javax.persistence.*;

@Entity
@Table(name = "ba_factor_desescolarizacion")
public class FactorDesescolarizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rango_edad", length = 30)
    private String rangoEdad;

    @Column(name = "razon_principal", length = 150)
    private String razonPrincipal;

    @Column(name = "razon_adicional", length = 500)
    private String razonAdicional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_id", nullable = false)
    private RegistroBusquedaActiva registro;

    public FactorDesescolarizacion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRangoEdad() { return rangoEdad; }
    public void setRangoEdad(String rangoEdad) { this.rangoEdad = rangoEdad; }
    public String getRazonPrincipal() { return razonPrincipal; }
    public void setRazonPrincipal(String razonPrincipal) { this.razonPrincipal = razonPrincipal; }
    public String getRazonAdicional() { return razonAdicional; }
    public void setRazonAdicional(String razonAdicional) { this.razonAdicional = razonAdicional; }
    public RegistroBusquedaActiva getRegistro() { return registro; }
    public void setRegistro(RegistroBusquedaActiva registro) { this.registro = registro; }
}
