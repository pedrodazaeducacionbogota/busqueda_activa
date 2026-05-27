package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAInfoTablaDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ResponseBASeccionesDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BusquedaActivaFormularioRepository;
import co.gov.educacionbogota.sicobertura.entities.ConfiguracionEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.Usuario;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;
import co.gov.educacionbogota.sicobertura.servicesimpl.UsuarioService;

/**
 * CRUD básico formulario BA (4 etapas). Crear, leer, listar.
 * Vigencia + etapa se leen de Configuracion (VIGENCIA, ETAPA).
 */
@Service
public class BAFormularioService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String CFG_VIGENCIA = "VIGENCIA";
    private static final String CFG_ETAPA = "ETAPA";

    @Autowired private BusquedaActivaFormularioRepository formularioRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;
    @Autowired private UsuarioService usuarioService;

    @Transactional
    public ResponseBASeccionesDto crearFormulario() {
        Usuario profesional = usuarioService.findByLogeado();
        if (profesional == null) {
            throw new ReglaNegocioException("No hay usuario autenticado");
        }
        BusquedaActivaFormularioEntity nuevo = new BusquedaActivaFormularioEntity();
        nuevo.setVigencia(leerConfigInt(CFG_VIGENCIA));
        nuevo.setEtapa(leerConfigInt(CFG_ETAPA));
        nuevo.setFinalizado(false);
        nuevo.setUltimaEtapa(0);
        nuevo.setProfesional(profesional);

        BusquedaActivaFormularioEntity guardado = formularioRepository.save(nuevo);
        return new ResponseBASeccionesDto(guardado.getId(), profesional.getId());
    }

    @Transactional(readOnly = true)
    public BusquedaActivaFormularioEntity getFormulario(Long id) {
        return formularioRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Formulario BA no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<BAInfoTablaDto> listarPorProfesional() {
        Usuario profesional = usuarioService.findByLogeado();
        return formularioRepository.findByProfesionalOrderByFechaCreaDesc(profesional).stream()
                .map(this::toInfoTabla).collect(Collectors.toList());
    }

    private BAInfoTablaDto toInfoTabla(BusquedaActivaFormularioEntity f) {
        BAInfoTablaDto dto = new BAInfoTablaDto();
        dto.setId(f.getId());
        dto.setFechaCrea(f.getFechaCrea() != null ? f.getFechaCrea().format(FECHA_FMT) : null);
        dto.setFinalizado(f.isFinalizado());
        dto.setUltimaEtapa(f.getUltimaEtapa());
        dto.setEstado(f.isFinalizado() ? "Finalizado" : "Pendiente");

        if (f.getEstudiante() != null && f.getEstudiante().getPersona() != null) {
            PersonaEntity p = f.getEstudiante().getPersona();
            dto.setEstudiante(joinNombre(p.getPrimerNombre(), p.getPrimerApellido()));
            if (p.getPoblacionDiferencial() != null) dto.setPoblacion(p.getPoblacionDiferencial().getNombre());
            if (p.getUbicacion() != null) {
                if (p.getUbicacion().getLocalidad() != null) dto.setLocalidad(p.getUbicacion().getLocalidad().getNombre());
                if (p.getUbicacion().getBarrio() != null) dto.setBarrio(p.getUbicacion().getBarrio().getNombre());
                else dto.setBarrio(p.getUbicacion().getBarrioOtro());
            }
        }
        return dto;
    }

    private String joinNombre(String n, String a) {
        StringBuilder sb = new StringBuilder();
        if (n != null) sb.append(n);
        if (a != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(a);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Transactional
    public BusquedaActivaFormularioEntity guardar(BusquedaActivaFormularioEntity f) {
        return formularioRepository.save(f);
    }

    private int leerConfigInt(String nombre) {
        ConfiguracionEntity cfg = configuracionRepository.findByNombreConfiguracion(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Configuración no encontrada: " + nombre));
        try {
            return Integer.parseInt(cfg.getValor().trim());
        } catch (NumberFormatException ex) {
            throw new ReglaNegocioException("Configuración " + nombre + " no es numérica: " + cfg.getValor());
        }
    }
}
