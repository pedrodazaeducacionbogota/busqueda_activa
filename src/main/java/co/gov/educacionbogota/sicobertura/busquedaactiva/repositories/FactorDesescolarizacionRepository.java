package co.gov.educacionbogota.sicobertura.busquedaactiva.repositories;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.FactorDesescolarizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactorDesescolarizacionRepository extends JpaRepository<FactorDesescolarizacion, Long> {
}
