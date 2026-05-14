package co.gov.educacionbogota.sicobertura.busquedaactiva.repositories;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.CaracterizacionEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaracterizacionRepository extends JpaRepository<CaracterizacionEntity, Long> {
    Optional<CaracterizacionEntity> findBySolicitud(SolicitudEntity solicitud);
}
