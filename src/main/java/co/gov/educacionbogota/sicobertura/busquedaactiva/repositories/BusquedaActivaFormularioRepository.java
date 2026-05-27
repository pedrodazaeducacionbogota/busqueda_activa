package co.gov.educacionbogota.sicobertura.busquedaactiva.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.entities.Usuario;

@Repository
public interface BusquedaActivaFormularioRepository extends JpaRepository<BusquedaActivaFormularioEntity, Long> {

    /** Lista formularios creados por el profesional autenticado. */
    List<BusquedaActivaFormularioEntity> findByProfesionalOrderByFechaCreaDesc(Usuario profesional);

    /** Dedup por documento acudiente en misma vigencia+etapa. */
    @Query("SELECT f FROM BusquedaActivaFormularioEntity f WHERE f.vigencia = :vigencia "
         + "AND f.etapa = :etapa AND f.acudiente.numeroDocumento = :documento")
    List<BusquedaActivaFormularioEntity> buscarPorAcudiente(@Param("documento") String documento,
            @Param("etapa") Integer etapa, @Param("vigencia") Integer vigencia);

    /** Dedup por documento estudiante en misma vigencia+etapa. */
    @Query("SELECT f FROM BusquedaActivaFormularioEntity f WHERE f.vigencia = :vigencia "
         + "AND f.etapa = :etapa AND f.estudiante.persona.numeroDocumento = :documento")
    List<BusquedaActivaFormularioEntity> buscarPorEstudiante(@Param("documento") String documento,
            @Param("etapa") Integer etapa, @Param("vigencia") Integer vigencia);
}
