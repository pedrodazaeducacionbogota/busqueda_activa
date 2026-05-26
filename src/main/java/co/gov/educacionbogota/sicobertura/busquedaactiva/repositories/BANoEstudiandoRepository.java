package co.gov.educacionbogota.sicobertura.busquedaactiva.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BANoEstudiandoEntity;

@Repository
public interface BANoEstudiandoRepository extends JpaRepository<BANoEstudiandoEntity, Long> {

    List<BANoEstudiandoEntity> findByFormularioId(Long formularioId);

    Optional<BANoEstudiandoEntity> findByFormularioIdAndRangoEdadCodigo(Long formularioId, String rangoEdadCodigo);
}
