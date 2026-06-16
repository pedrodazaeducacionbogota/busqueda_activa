package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.gov.educacionbogota.sicobertura.dto.ApiResponseDto;
import co.gov.educacionbogota.sicobertura.dto.RefListadoItemResponseDto;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ref-listados")
@Tag(name = "08. Ref Listados", description = "Consulta de listados de referencia (id-based)")
public class RefListadoController {

    private static final String CODIGO_OTRO = "OTRO";
    private static final String CODIGO_OTRA = "OTRA";
    private static final String NOMBRE_OTRO = "Otro";
    private static final String NOMBRE_OTRA = "Otra";

    private static final java.util.Set<String> TIPOS_FEMENINOS = new java.util.HashSet<>(
            java.util.Arrays.asList("ETNIAS", "OCUPACIONES", "DISCAPACIDADES"));

    @Autowired
    private RefListadoRepository refListadoRepository;

    @GetMapping("/{idPadre}/hijos/tipo/{tipo}")
    @Operation(summary = "Hijos del listado idPadre filtrados por tipo (descripcion). Incluye OTRO/OTRA al final.")
    public ResponseEntity<ApiResponseDto> hijosPorTipo(
            @PathVariable Long idPadre,
            @PathVariable String tipo) {

        List<RefListadoItemResponseDto> items = refListadoRepository
                .findHijosByIdPadreAndTipo(idPadre, tipo)
                .stream()
                .map(r -> RefListadoItemResponseDto.builder()
                        .id(r.getIdRefListado())
                        .codigo(r.getCodigo())
                        .nombre(r.getNombre())
                        .descripcion(r.getDescripcion())
                        .orden(r.getOrden())
                        .habilita(r.getHabilita())
                        .aux1(r.getAux1())
                        .aux2(r.getAux2())
                        .aux3(r.getAux3())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        boolean femenino = TIPOS_FEMENINOS.contains(tipo.toUpperCase());
        items.add(RefListadoItemResponseDto.builder()
                .codigo(femenino ? CODIGO_OTRA : CODIGO_OTRO)
                .nombre(femenino ? NOMBRE_OTRA : NOMBRE_OTRO)
                .descripcion(tipo)
                .orden(Integer.MAX_VALUE)
                .build());

        return ResponseEntity.ok(new ApiResponseDto(true, "Consulta", items));
    }
}
