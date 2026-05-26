package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAInfoTablaDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ResponseBASeccionesDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BANoEstudiandoEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BusquedaActivaFormularioRepository;
import co.gov.educacionbogota.sicobertura.entities.ConfiguracionEntity;
import co.gov.educacionbogota.sicobertura.entities.Usuario;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;
import co.gov.educacionbogota.sicobertura.servicesimpl.UsuarioService;

/**
 * CRUD básico formulario BA. Crear, leer, listar, agregar familiar.
 * Vigencia + etapa se leen de Configuracion (B_ACTIVA_VIGENCIA, B_ACTIVA_ETAPA).
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
        int vigencia = leerConfigInt(CFG_VIGENCIA);
        int etapa = leerConfigInt(CFG_ETAPA);

        BusquedaActivaFormularioEntity nuevo = new BusquedaActivaFormularioEntity();
        nuevo.setVigencia(vigencia);
        nuevo.setEtapa(etapa);
        nuevo.setFinalizado(false);
        nuevo.setUltimaSeccion(0);
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
        dto.setUltimaSeccion(f.getUltimaSeccion());
        if (f.getActividad() != null) dto.setActividad(f.getActividad().getCodigo());
        dto.setNombreEventoFeria(f.getNombreEventoFeria());
        dto.setPoblacion(f.getPoblacionEvento());
        if (f.getUbicacion() != null) {
            if (f.getUbicacion().getLocalidad() != null) dto.setLocalidad(f.getUbicacion().getLocalidad().getCodigo());
            if (f.getUbicacion().getBarrio() != null) dto.setBarrio(f.getUbicacion().getBarrio().getCodigo());
        }
        if (f.getAtiendeVisita() != null) {
            dto.setAtiendeVisita(joinNombre(f.getAtiendeVisita().getPrimerNombre(), f.getAtiendeVisita().getPrimerApellido()));
        }
        if (f.getEstudiante() != null && f.getEstudiante().getPersona() != null) {
            dto.setEstudiante(joinNombre(f.getEstudiante().getPersona().getPrimerNombre(),
                    f.getEstudiante().getPersona().getPrimerApellido()));
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

    /**
     * Clona formulario preservando secciones 1-5 (visita, atiende, colegios, no_estudiando, acudiente).
     * Reset ultimaSeccion=5. Sin estudiante ni sección 7. Caso: 1 acudiente + varios hijos por familia.
     */
    @Transactional
    public ResponseBASeccionesDto agregarFamiliar(Long id) {
        BusquedaActivaFormularioEntity src = getFormulario(id);
        BusquedaActivaFormularioEntity clon = new BusquedaActivaFormularioEntity();
        clon.setVigencia(src.getVigencia());
        clon.setEtapa(src.getEtapa());
        clon.setProfesional(src.getProfesional());
        clon.setFinalizado(false);
        clon.setUltimaSeccion(5);

        // Sección 1
        clon.setActividad(src.getActividad());
        clon.setActividadOtra(src.getActividadOtra());
        clon.setNombreEventoFeria(src.getNombreEventoFeria());
        clon.setPoblacionEvento(src.getPoblacionEvento());
        clon.setUbicacion(src.getUbicacion());
        // Sección 2
        clon.setAtiendeVisita(src.getAtiendeVisita());
        // Sección 3
        clon.setColegiosCerca(src.isColegiosCerca());
        clon.setIdeColegioCerca(src.getIdeColegioCerca());
        clon.setColegioCercaCual(src.getColegioCercaCual());
        clon.setJardinesCerca(src.isJardinesCerca());
        clon.setIdeJardinCerca(src.getIdeJardinCerca());
        clon.setJardinCercaCual(src.getJardinCercaCual());
        // Sección 4 — copia profunda (nuevos rows con FK al clon)
        for (BANoEstudiandoEntity rango : src.getNoEstudiandoRangos()) {
            BANoEstudiandoEntity copia = new BANoEstudiandoEntity();
            copia.setRangoEdadCodigo(rango.getRangoEdadCodigo());
            copia.setCuantos(rango.getCuantos());
            copia.setRazon(rango.getRazon());
            copia.setRazonOtra(rango.getRazonOtra());
            clon.agregarNoEstudiando(copia);
        }
        // Sección 5
        clon.setAtiendeVisitaAcudiente(src.isAtiendeVisitaAcudiente());
        clon.setAcudiente(src.getAcudiente());

        BusquedaActivaFormularioEntity guardado = formularioRepository.save(clon);
        return new ResponseBASeccionesDto(guardado.getId(), src.getProfesional().getId());
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
