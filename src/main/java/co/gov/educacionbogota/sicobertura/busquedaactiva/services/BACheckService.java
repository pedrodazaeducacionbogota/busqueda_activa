package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ValidarDocumentoDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BusquedaActivaFormularioRepository;
import co.gov.educacionbogota.sicobertura.entities.ConfiguracionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;

/**
 * Validación documento (HU-004 paso 5). Dedup por documento + etapa + vigencia.
 * tipo ∈ {ESTUDIANTE, ACUDIENTE}. Si ya existe, retorna fecha + profesional que registró
 * para el modal informativo del front.
 */
@Service
public class BACheckService {

    private static final String CFG_VIGENCIA = "VIGENCIA";
    private static final String CFG_ETAPA = "ETAPA";
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired private BusquedaActivaFormularioRepository formularioRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;

    @Transactional(readOnly = true)
    public ValidarDocumentoDto validarDocumento(String documento, String tipo) {
        int vigencia = leerConfigInt(CFG_VIGENCIA);
        int etapa = leerConfigInt(CFG_ETAPA);

        List<BusquedaActivaFormularioEntity> matches;
        switch (tipo) {
            case "ESTUDIANTE":
                matches = formularioRepository.buscarPorEstudiante(documento, etapa, vigencia);
                break;
            case "ACUDIENTE":
                matches = formularioRepository.buscarPorAcudiente(documento, etapa, vigencia);
                break;
            default:
                throw new ReglaNegocioException("Tipo inválido: " + tipo + " (esperados: ESTUDIANTE/ACUDIENTE)");
        }

        ValidarDocumentoDto dto = new ValidarDocumentoDto();
        if (matches == null || matches.isEmpty()) {
            dto.setNuevo(true);
        } else {
            BusquedaActivaFormularioEntity existente = matches.get(0);
            dto.setNuevo(false);
            dto.setIdFormulario(existente.getId());
            if (existente.getFechaCrea() != null) {
                dto.setFechaRegistro(existente.getFechaCrea().format(FECHA_FMT));
            }
            if (existente.getProfesional() != null) {
                dto.setRegistradoPor(existente.getProfesional().getNombreUsuario());
            }
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
