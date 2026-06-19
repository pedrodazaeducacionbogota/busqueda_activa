package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.GradosAprobadosDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.entities.EdadGradoEntity;
import co.gov.educacionbogota.sicobertura.entities.GradoEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.EdadGradoRepository;
import co.gov.educacionbogota.sicobertura.repository.GradoRepository;

/**
 * Helper edad → grado para wizard sección 7 (cupo solicitud).
 * - gradosAprobados(idFormulario): edad+1 del estudiante (clamp 3..18, ≥18→99),
 *   retorna lista distinct de grados previos elegibles (edad_grado.id_ref_grado_previo).
 * - gradosSolicitados(idFormulario, codigoGradoPrevio): dado edad + grado aprobado,
 *   retorna grados solicitables (edad_grado.id_ref_grado_solicitado).
 */
@Service
public class BAEdadGradoService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired private BAFormularioService formularioService;
    @Autowired private EdadGradoRepository edadGradoRepository;
    @Autowired private GradoRepository gradoRepository;

    @Transactional(readOnly = true)
    public GradosAprobadosDto gradosAprobados(Long idFormulario) {
        int edadConsulta = calcularEdadConsulta(idFormulario);
        List<EdadGradoEntity> reglas = edadGradoRepository.findByEdadAndActivo(edadConsulta, 1);

        // Distinct gradoPrevio
        Set<Long> idsGradoPrevio = new HashSet<>();
        List<GradosAprobadosDto.RefListadoLiteDto> grados = new ArrayList<>();
        for (EdadGradoEntity regla : reglas) {
            if (idsGradoPrevio.add(regla.getIdRefGradoPrevio())) {
                gradoRepository.findById(regla.getIdRefGradoPrevio())
                        .ifPresent(g -> grados.add(new GradosAprobadosDto.RefListadoLiteDto(
                                g.getId(), String.valueOf(g.getCodigo()), "GRADOS_ESCOLARES")));
            }
        }
        return new GradosAprobadosDto(edadConsulta, grados);
    }

    @Transactional(readOnly = true)
    public List<GradosAprobadosDto.RefListadoLiteDto> gradosSolicitados(Long idFormulario, String codigoGradoPrevio) {
        int edadConsulta = calcularEdadConsulta(idFormulario);
        GradoEntity gradoPrevio = gradoRepository
                .findByCodigoAndActivo(Integer.valueOf(codigoGradoPrevio), true)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Grado previo no encontrado: " + codigoGradoPrevio));

        List<EdadGradoEntity> reglas = edadGradoRepository
                .findByEdadAndIdRefGradoPrevioAndActivo(edadConsulta, gradoPrevio.getId(), 1);

        List<GradosAprobadosDto.RefListadoLiteDto> grados = new ArrayList<>();
        Set<Long> vistos = new HashSet<>();
        for (EdadGradoEntity regla : reglas) {
            if (vistos.add(regla.getIdRefGradoSolicitado())) {
                gradoRepository.findById(regla.getIdRefGradoSolicitado())
                        .ifPresent(g -> grados.add(new GradosAprobadosDto.RefListadoLiteDto(
                                g.getId(), String.valueOf(g.getCodigo()), "GRADOS_ESCOLARES")));
            }
        }
        return grados;
    }

    /** edad estudiante + 1, clamp [3..18], ≥18 → 99. Refleja política SED para asignar cupo año siguiente. */
    private int calcularEdadConsulta(Long idFormulario) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(idFormulario);
        if (f.getEstudiante() == null || f.getEstudiante().getPersona() == null
                || f.getEstudiante().getPersona().getFechaNacimientoStr() == null) {
            throw new ReglaNegocioException("Formulario sin estudiante o sin fecha nacimiento (sección 6 incompleta)");
        }
        LocalDate fechaNac = LocalDate.parse(f.getEstudiante().getPersona().getFechaNacimientoStr(), FECHA_FMT);
        int edad = Period.between(fechaNac, LocalDate.now()).getYears() + 1;
        if (edad < 3) edad = 3;
        if (edad >= 18) edad = 99;
        return edad;
    }

}
