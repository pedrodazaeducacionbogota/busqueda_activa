package co.gov.educacionbogota.sicobertura.busquedaactiva.repositories;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroBusquedaActivaRepository extends JpaRepository<RegistroBusquedaActiva, Long> {
    List<RegistroBusquedaActiva> findByEstado(String estado);
    List<RegistroBusquedaActiva> findByUltimaEtapaRegistrada(Integer ultimaEtapaRegistrada);
    List<RegistroBusquedaActiva> findByEstadoAndUltimaEtapaRegistrada(String estado, Integer ultimaEtapaRegistrada);
}
