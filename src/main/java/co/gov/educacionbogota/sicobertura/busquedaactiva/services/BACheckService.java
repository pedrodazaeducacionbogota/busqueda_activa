package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.CheckEstudianteDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BusquedaActivaFormularioRepository;
import co.gov.educacionbogota.sicobertura.entities.ConfiguracionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;

/**
 * Dedup por documento + etapa + vigencia. tipo ∈ {ATIENDE, ACUDIENTE, ESTUDIANTE}.
 * Front llama antes de iniciar wizard para evitar formularios duplicados misma familia.
 */
@Service
public class BACheckService {

    private static final String CFG_VIGENCIA = "B_ACTIVA_VIGENCIA";
    private static final String CFG_ETAPA = "B_ACTIVA_ETAPA";

    @Autowired private BusquedaActivaFormularioRepository formularioRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;

    @Transactional(readOnly = true)
    public CheckEstudianteDto checkEstudiante(String documento, String tipo) {
        int vigencia = leerConfigInt(CFG_VIGENCIA);
        int etapa = leerConfigInt(CFG_ETAPA);

        List<BusquedaActivaFormularioEntity> matches;
        switch (tipo) {
            case "ATIENDE":
                matches = formularioRepository.buscarPorAtiende(documento, etapa, vigencia);
                break;
            case "ACUDIENTE":
                matches = formularioRepository.buscarPorAcudiente(documento, etapa, vigencia);
                break;
            case "ESTUDIANTE":
                matches = formularioRepository.buscarPorEstudiante(documento, etapa, vigencia);
                break;
            default:
                throw new ReglaNegocioException("Tipo inválido: " + tipo + " (esperados: ATIENDE/ACUDIENTE/ESTUDIANTE)");
        }

        CheckEstudianteDto dto = new CheckEstudianteDto();
        if (matches == null || matches.isEmpty() || Collections.emptyList().equals(matches)) {
            dto.setNuevo(true);
        } else {
            dto.setNuevo(false);
            dto.setIdSolicitud(matches.get(0).getId());
        }
        return dto;
    }

    private int leerConfigInt(String nombre) {
        ConfiguracionEntity cfg = configuracionRepository.findByNombreConfiguracion(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Configuración no encontrada: " + nombre));
        try {
            return Integer.parseInt(cfg.getValor().trim());
        } catch (NumberFormatException ex) {
            throw new ReglaNegocioException("Configuración " + nombre + " no numérica: " + cfg.getValor());
        }
    }
}
