package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa1Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa2Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa3Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa4Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.IdeItemDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BACheckService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAEdadGradoService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAEtapaService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAFormularioService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAGetService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BARefResolverService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAPdfService;
import co.gov.educacionbogota.sicobertura.dto.ApiResponseDto;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.SedeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints wizard Búsqueda Activa (4 etapas + helpers) — HU 12-IF-049.
 * Auth dinámica via endpoint_permiso DB (no @PreAuthorize). Mapeo en endpoint_permisos.csv.
 */
@RestController
@RequestMapping("/api/busqueda-activa")
@Tag(name = "07. Búsqueda Activa", description = "Wizard 4-etapas formulario BA + helpers (PDF, validar documento, grados aprobados/solicitados)")
public class BusquedaActivaController {

    @Autowired private BAFormularioService formularioService;
    @Autowired private BAEtapaService etapaService;
    @Autowired private BAGetService getService;
    @Autowired private BACheckService checkService;
    @Autowired private BAEdadGradoService edadGradoService;
    @Autowired private BAPdfService pdfService;
    @Autowired private BARefResolverService resolver;
    @Autowired private SedeRepository sedeRepository;

    @GetMapping
    @Operation(summary = "Lista formularios BA del profesional autenticado (tarjetas HU-003)")
    public ResponseEntity<ApiResponseDto> listar() {
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", formularioService.listarPorProfesional()));
    }

    @GetMapping("/detalle/{id}/{etapa}")
    @Operation(summary = "Detalle por etapa (0-4). 0 retorna metadata; 1-4 retorna DTO de la etapa")
    public ResponseEntity<ApiResponseDto> detalle(@PathVariable Long id, @PathVariable Integer etapa) {
        Object data;
        switch (etapa) {
            case 0: data = getService.detalleEtapa0(id); break;
            case 1: data = getService.detalleEtapa1(id); break;
            case 2: data = getService.detalleEtapa2(id); break;
            case 3: data = getService.detalleEtapa3(id); break;
            case 4: data = getService.detalleEtapa4(id); break;
            default:
                throw new ReglaNegocioException("Etapa no reconocida: " + etapa + " (esperadas 0-4)");
        }
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", data));
    }

    @PostMapping
    @Operation(summary = "Crea formulario BA vacío. Lee vigencia + etapa de Configuracion (VIGENCIA/ETAPA)")
    public ResponseEntity<ApiResponseDto> crear() {
        return ResponseEntity.ok(new ApiResponseDto(true, "Formulario BA creado", formularioService.crearFormulario()));
    }

    @PutMapping("/etapa1/{id}")
    @Operation(summary = "Etapa 1 (HU-004): información sociodemográfica del estudiante")
    public ResponseEntity<ApiResponseDto> etapa1(@PathVariable Long id, @Valid @RequestBody BAEtapa1Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Etapa 1 actualizada", etapaService.actualizarEtapa1(id, dto)));
    }

    @PutMapping("/etapa2/{id}")
    @Operation(summary = "Etapa 2 (HU-005): solicitud cupo + hermanos + hasta 10 instituciones")
    public ResponseEntity<ApiResponseDto> etapa2(@PathVariable Long id, @Valid @RequestBody BAEtapa2Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Etapa 2 actualizada", etapaService.actualizarEtapa2(id, dto)));
    }

    @PutMapping("/etapa3/{id}")
    @Operation(summary = "Etapa 3 (HU-006): información de contacto del responsable o acudiente")
    public ResponseEntity<ApiResponseDto> etapa3(@PathVariable Long id, @Valid @RequestBody BAEtapa3Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Etapa 3 actualizada", etapaService.actualizarEtapa3(id, dto)));
    }

    @PutMapping("/etapa4/{id}")
    @Operation(summary = "Etapa 4 (HU-007): factores de descolarización. Marca finalizado=true")
    public ResponseEntity<ApiResponseDto> etapa4(@PathVariable Long id, @Valid @RequestBody BAEtapa4Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Etapa 4 actualizada", etapaService.actualizarEtapa4(id, dto)));
    }

    @GetMapping("/resumen/{id}")
    @Operation(summary = "PDF resumen formulario en base64. Estado FINAL si finalizado=true, sino PRELIMINAR")
    public ResponseEntity<ApiResponseDto> resumen(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDto(true, "PDF generado", pdfService.generarResumen(id)));
    }

    @GetMapping("/validar-documento/{tipo}/{numero}")
    @Operation(summary = "Dedup por documento + etapa + vigencia (HU-004 paso 5). tipo ∈ {ESTUDIANTE, ACUDIENTE}")
    public ResponseEntity<ApiResponseDto> validarDocumento(@PathVariable String tipo, @PathVariable String numero) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Validación OK", checkService.validarDocumento(numero, tipo)));
    }

    @GetMapping("/grados-aprobados/{id}")
    @Operation(summary = "Grados previos elegibles según edad+1 del estudiante (clamp 3..18, ≥18→99)")
    public ResponseEntity<ApiResponseDto> gradosAprobados(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", edadGradoService.gradosAprobados(id)));
    }

    @GetMapping("/grados-solicitados/{id}/grado-aprobado/{codigoGradoAprobado}")
    @Operation(summary = "Grados solicitables dado grado previo aprobado")
    public ResponseEntity<ApiResponseDto> gradosSolicitados(@PathVariable Long id,
                                                              @PathVariable String codigoGradoAprobado) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta",
                edadGradoService.gradosSolicitados(id, codigoGradoAprobado)));
    }

    @GetMapping("/colegios/por-localidad/{idLocalidad}")
    @Operation(summary = "Sedes activas filtradas por id_ref_listado de localidad (sede.localidad fallback ide.localidad)")
    public ResponseEntity<ApiResponseDto> colegiosPorLocalidad(@PathVariable Long idLocalidad) {
        resolver.resolveRequired(idLocalidad, "LOCALIDAD");
        List<IdeItemDto> items = sedeRepository
                .findActivasPorLocalidad(idLocalidad)
                .stream()
                .map(s -> IdeItemDto.builder()
                        .id(s.getId())
                        .nombreSede(s.getNombre())
                        .nombreColegio(s.getIde().getNombre())
                        .codigoDane(s.getCodigoDane())
                        .direccion(s.getDireccion())
                        .longitud(s.getLongitudX() != null ? s.getLongitudX().toString() : null)
                        .latitud(s.getLatitudY() != null ? s.getLatitudY().toString() : null)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", items));
    }
}
