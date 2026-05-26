package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion1Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion2Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion3Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion4Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion5Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion6Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion7Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BACheckService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAEdadGradoService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAFormularioService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAGetService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BAPdfService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BASeccionService;
import co.gov.educacionbogota.sicobertura.dto.ApiResponseDto;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints wizard Búsqueda Activa (7 secciones + helpers).
 * Auth dinámica via endpoint_permiso DB (no @PreAuthorize). Roles permitidos:
 * RECTOR/PSI/FUNCIONARIO_INST/FUNCIONARIO_SED/ADMIN. Mapeo en endpoint_permisos.csv.
 */
@RestController
@RequestMapping("/api/busqueda-activa")
@Tag(name = "07. Búsqueda Activa", description = "Wizard 7-secciones formulario BA + helpers (PDF, check estudiante, grados aprobados/solicitados)")
public class BusquedaActivaController {

    @Autowired private BAFormularioService formularioService;
    @Autowired private BASeccionService seccionService;
    @Autowired private BAGetService getService;
    @Autowired private BACheckService checkService;
    @Autowired private BAEdadGradoService edadGradoService;
    @Autowired private BAPdfService pdfService;

    @GetMapping
    @Operation(summary = "Lista formularios BA del profesional autenticado")
    public ResponseEntity<ApiResponseDto> listar() {
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", formularioService.listarPorProfesional()));
    }

    @GetMapping("/detalle/{id}/{seccion}")
    @Operation(summary = "Detalle por sección (0-7). 0 retorna metadata; 1-7 retorna DTO de la sección")
    public ResponseEntity<ApiResponseDto> detalle(@PathVariable Long id, @PathVariable Integer seccion) {
        Object data;
        switch (seccion) {
            case 0: data = getService.detalleSeccion0(id); break;
            case 1: data = getService.detalleSeccion1(id); break;
            case 2: data = getService.detalleSeccion2(id); break;
            case 3: data = getService.detalleSeccion3(id); break;
            case 4: data = getService.detalleSeccion4(id); break;
            case 5: data = getService.detalleSeccion5(id); break;
            case 6: data = getService.detalleSeccion6(id); break;
            case 7: data = getService.detalleSeccion7(id); break;
            default:
                throw new ReglaNegocioException("Sección no reconocida: " + seccion + " (esperadas 0-7)");
        }
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", data));
    }

    @PostMapping
    @Operation(summary = "Crea formulario BA vacío. Lee vigencia + etapa de Configuracion B_ACTIVA_*")
    public ResponseEntity<ApiResponseDto> crear() {
        return ResponseEntity.ok(new ApiResponseDto(true, "Formulario BA creado", formularioService.crearFormulario()));
    }

    @PutMapping("/seccion1/{id}")
    @Operation(summary = "Actualiza sección 1 (actividad + ubicación visita)")
    public ResponseEntity<ApiResponseDto> seccion1(@PathVariable Long id, @Valid @RequestBody BASeccion1Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 1 actualizada", seccionService.actualizarSeccion1(id, dto)));
    }

    @PutMapping("/seccion2/{id}")
    @Operation(summary = "Actualiza sección 2 (persona atiende visita)")
    public ResponseEntity<ApiResponseDto> seccion2(@PathVariable Long id, @Valid @RequestBody BASeccion2Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 2 actualizada", seccionService.actualizarSeccion2(id, dto)));
    }

    @PutMapping("/seccion3/{id}")
    @Operation(summary = "Actualiza sección 3 (colegios/jardines cerca)")
    public ResponseEntity<ApiResponseDto> seccion3(@PathVariable Long id, @Valid @RequestBody BASeccion3Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 3 actualizada", seccionService.actualizarSeccion3(id, dto)));
    }

    @PutMapping("/seccion4/{id}")
    @Operation(summary = "Actualiza sección 4 (no estudiando por rango edad — normalizada)")
    public ResponseEntity<ApiResponseDto> seccion4(@PathVariable Long id, @Valid @RequestBody BASeccion4Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 4 actualizada", seccionService.actualizarSeccion4(id, dto)));
    }

    @PutMapping("/seccion5/{id}")
    @Operation(summary = "Actualiza sección 5 (acudiente). atiendeVisitaAcudiente=true reusa persona sección 2")
    public ResponseEntity<ApiResponseDto> seccion5(@PathVariable Long id, @Valid @RequestBody BASeccion5Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 5 actualizada", seccionService.actualizarSeccion5(id, dto)));
    }

    @PutMapping("/seccion6/{id}")
    @Operation(summary = "Actualiza sección 6 (estudiante con persona nested + edad calculada)")
    public ResponseEntity<ApiResponseDto> seccion6(@PathVariable Long id, @Valid @RequestBody BASeccion6Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 6 actualizada", seccionService.actualizarSeccion6(id, dto)));
    }

    @PutMapping("/seccion7/{id}")
    @Operation(summary = "Actualiza sección 7 (educativo + solicitud cupo). Marca finalizado=true")
    public ResponseEntity<ApiResponseDto> seccion7(@PathVariable Long id, @Valid @RequestBody BASeccion7Dto dto) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Sección 7 actualizada", seccionService.actualizarSeccion7(id, dto)));
    }

    @GetMapping("/resumen/{id}")
    @Operation(summary = "PDF resumen formulario en base64. Estado FINAL si finalizado=true, sino PRELIMINAR")
    public ResponseEntity<ApiResponseDto> resumen(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDto(true, "PDF generado", pdfService.generarResumen(id)));
    }

    @PutMapping("/agregar-familiar/{id}")
    @Operation(summary = "Clona formulario preservando secciones 1-5. Nuevo wizard para otro estudiante misma familia")
    public ResponseEntity<ApiResponseDto> agregarFamiliar(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Familiar agregado", formularioService.agregarFamiliar(id)));
    }

    @GetMapping("/check-estudiante/{documento}/tipo/{tipo}")
    @Operation(summary = "Dedup por documento + etapa + vigencia. tipo ∈ {ATIENDE, ACUDIENTE, ESTUDIANTE}")
    public ResponseEntity<ApiResponseDto> checkEstudiante(@PathVariable String documento, @PathVariable String tipo) {
        return ResponseEntity.ok(new ApiResponseDto(true, "Check OK", checkService.checkEstudiante(documento, tipo)));
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
}
