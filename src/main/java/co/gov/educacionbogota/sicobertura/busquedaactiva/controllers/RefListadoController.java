package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.gov.educacionbogota.sicobertura.dto.ApiResponseDto;
import co.gov.educacionbogota.sicobertura.dto.RefListadoItemResponseDto;
import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ref-listados")
@Tag(name = "08. Ref Listados", description = "Consulta de listados de referencia")
public class RefListadoController {

    private static final String CODIGO_OTRO = "OTRO";
    private static final String NOMBRE_OTRO = "Otro";

    @Autowired
    private RefListadoRepository refListadoRepository;

    // ── Búsqueda directa ─────────────────────────────────────────────────────

    @GetMapping("/directo/{codigo}")
    @Operation(summary = "Búsqueda directa por código. Retorna {id, codigo, valorTxt, valorInt}.")
    public ResponseEntity<ApiResponseDto> directoPorCodigo(@PathVariable String codigo) {
        RefListadoKVDto kv = refListadoRepository.findByCodigoAndActivo(codigo, 1)
                .map(this::toKV)
                .orElseThrow(() -> new RecursoNoEncontradoException("RefListado no encontrado: " + codigo));
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", kv));
    }

    @GetMapping("/directos")
    @Operation(summary = "Búsqueda directa por lista de códigos. Retorna List<{id, codigo, valorTxt, valorInt}>.")
    public ResponseEntity<ApiResponseDto> directosPorCodigos(@RequestParam List<String> codigos) {
        List<RefListadoKVDto> items = refListadoRepository.findByCodigoInAndActivo(codigos, 1)
                .stream()
                .map(this::toKV)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", items));
    }

    // ── Hijos por tipo ────────────────────────────────────────────────────────

    @GetMapping("/{codigoListado}/hijos/tipo/{tipo}")
    @Operation(summary = "Hijos de un listado filtrados por tipo. Incluye opción OTRO al final.")
    public ResponseEntity<ApiResponseDto> hijosPorTipo(
            @PathVariable String codigoListado,
            @PathVariable String tipo) {

        List<RefListadoItemResponseDto> items = refListadoRepository
                .findHijosByCodigoPadreAndTipo(codigoListado, tipo)
                .stream()
                .map(r -> RefListadoItemResponseDto.builder()
                        .id(r.getIdRefListado())
                        .codigo(r.getCodigo())
                        .nombre(r.getNombre())
                        .descripcion(r.getDescripcion())
                        .valorTxt(r.getValorTxt())
                        .valorInt(r.getValorInt())
                        .orden(r.getOrden())
                        .habilita(r.getHabilita())
                        .aux1(r.getAux1())
                        .aux2(r.getAux2())
                        .aux3(r.getAux3())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        items.add(RefListadoItemResponseDto.builder()
                .codigo(CODIGO_OTRO)
                .nombre(NOMBRE_OTRO)
                .descripcion(tipo)
                .orden(Integer.MAX_VALUE)
                .build());

        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", items));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private RefListadoKVDto toKV(co.gov.educacionbogota.sicobertura.entities.RefListado r) {
        return RefListadoKVDto.builder()
                .id(r.getIdRefListado())
                .codigo(r.getCodigo())
                .valorTxt(r.getValorTxt())
                .valorInt(r.getValorInt())
                .build();
    }
}
