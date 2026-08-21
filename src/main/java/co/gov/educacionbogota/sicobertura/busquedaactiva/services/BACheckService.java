package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ValidarDocumentoDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BusquedaActivaFormularioRepository;
import co.gov.educacionbogota.sicobertura.entities.Anexo6aEntity;
import co.gov.educacionbogota.sicobertura.entities.ConfiguracionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.Anexo6aRepository;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;

/**
 * Validación documento (HU-004 paso 5).
 * Dedup por documento + etapa + vigencia (formulario BA propio previo).
 * Bloqueo estudiante ya matriculado SIMAT via Anexo6A (misma politica que inscripciones).
 * tipo ∈ {ESTUDIANTE, ACUDIENTE}. Anexo6A whitelist solo aplica ESTUDIANTE.
 */
@Service
public class BACheckService {

    private static final String CFG_VIGENCIA = "VIGENCIA";
    private static final String CFG_ETAPA = "ETAPA";
    private static final String TIPO_ESTUDIANTE = "ESTUDIANTE";
    private static final String TIPO_ACUDIENTE = "ACUDIENTE";
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Estados SIMAT (Anexo6A) que bloquean nueva caracterizacion BA — estudiante ya
     * vinculado al sistema. Estados NO listados (RETIRADO/CANCELADO/REPROBADO/TRASLADADO)
     * permiten caracterizar. Misma whitelist que inscripciones.
     */
    private static final Set<String> ESTADOS_SIMAT_BLOQUEAN = new HashSet<>(Arrays.asList(
            "MATRICULADO",
            "ASIGNADO",
            "ASIGNADO POR CONTINUIDAD",
            "NUEVO",
            "INSCRITO",
            "GRADUADO"
    ));

    @Autowired private BusquedaActivaFormularioRepository formularioRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;
    @Autowired private Anexo6aRepository anexo6aRepository;

    /**
     * Validación consultiva pre-registro. NO lanza excepcion — retorna DTO con flags
     * para que front decida UX (modal informativo).
     * @param tipoDoc opcional (idRefListado); si null y tipo=ESTUDIANTE, busca Anexo6A solo por numero
     */
    @Transactional(readOnly = true)
    public ValidarDocumentoDto validarDocumento(String documento, String tipo, Long tipoDoc) {
        int vigencia = leerConfigInt(CFG_VIGENCIA);
        int etapa = leerConfigInt(CFG_ETAPA);

        List<BusquedaActivaFormularioEntity> matches;
        switch (tipo) {
            case TIPO_ESTUDIANTE:
                matches = (tipoDoc != null)
                        ? formularioRepository.buscarPorEstudianteConTipo(documento, tipoDoc, etapa, vigencia)
                        : formularioRepository.buscarPorEstudiante(documento, etapa, vigencia);
                break;
            case TIPO_ACUDIENTE:
                matches = (tipoDoc != null)
                        ? formularioRepository.buscarPorAcudienteConTipo(documento, tipoDoc, etapa, vigencia)
                        : formularioRepository.buscarPorAcudiente(documento, etapa, vigencia);
                break;
            default:
                throw new ReglaNegocioException("Tipo inválido: " + tipo + " (esperados: ESTUDIANTE/ACUDIENTE)");
        }

        ValidarDocumentoDto dto = new ValidarDocumentoDto();
        dto.setNuevo(true);
        if (matches != null && !matches.isEmpty()) {
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

        if (TIPO_ESTUDIANTE.equals(tipo)) {
            Optional<Anexo6aEntity> anexo = (tipoDoc != null)
                    ? anexo6aRepository.findFirstByTipoDocumento_IdRefListadoAndNumeroDocumento(tipoDoc, documento)
                    : anexo6aRepository.findFirstByNumeroDocumento(documento);
            // Solo bloquear sector OFICIAL. Privados permiten caracterizar.
            anexo.filter(a -> a.getEstadoSimat() != null
                            && "OFICIAL".equalsIgnoreCase(a.getSector())
                            && ESTADOS_SIMAT_BLOQUEAN.contains(a.getEstadoSimat().toUpperCase()))
                    .ifPresent(a -> {
                        dto.setMatriculadoSimat(true);
                        dto.setEstadoSimat(a.getEstadoSimat());
                        dto.setNuevo(false);
                    });
        }

        return dto;
    }

    /**
     * Bloqueo duro backend etapa1 — lanza excepcion si estudiante tiene registro BA
     * previo (misma vigencia+etapa) o esta matriculado SIMAT.
     */
    @Transactional(readOnly = true)
    public void asegurarPuedeCaracterizar(Long tipoDoc, String numeroDoc) {
        int vigencia = leerConfigInt(CFG_VIGENCIA);
        int etapa = leerConfigInt(CFG_ETAPA);

        List<BusquedaActivaFormularioEntity> previos = (tipoDoc != null)
                ? formularioRepository.buscarPorEstudianteConTipo(numeroDoc, tipoDoc, etapa, vigencia)
                : formularioRepository.buscarPorEstudiante(numeroDoc, etapa, vigencia);
        if (previos != null && !previos.isEmpty()) {
            throw new ReglaNegocioException(
                    "El estudiante ya tiene un formulario BA registrado en esta vigencia/etapa (id="
                            + previos.get(0).getId() + ").");
        }

        if (tipoDoc != null) {
            anexo6aRepository
                    .findFirstByTipoDocumento_IdRefListadoAndNumeroDocumento(tipoDoc, numeroDoc)
                    .filter(a -> a.getEstadoSimat() != null
                            && "OFICIAL".equalsIgnoreCase(a.getSector())
                            && ESTADOS_SIMAT_BLOQUEAN.contains(a.getEstadoSimat().toUpperCase()))
                    .ifPresent(a -> {
                        throw new ReglaNegocioException(
                                "El estudiante figura en el sistema SIMAT con estado "
                                        + a.getEstadoSimat()
                                        + ". No se puede caracterizar. Contacte a la Secretaría de Educación.");
                    });
        }
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
