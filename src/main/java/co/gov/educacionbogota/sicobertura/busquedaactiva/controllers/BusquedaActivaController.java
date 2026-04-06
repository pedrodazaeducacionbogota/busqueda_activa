package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BusquedaActivaService;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/busqueda-activa")
@SuppressWarnings("null")
public class BusquedaActivaController {

    @Autowired
    private BusquedaActivaService service;

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/registros")
    public ResponseEntity<List<RegistroBusquedaActiva>> consultarRegistros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer etapa) {
        return ResponseEntity.ok(service.consultarRegistros(estado, etapa));
    }

    @GetMapping("/registros/{id}")
    public ResponseEntity<RegistroBusquedaActiva> obtenerRegistro(@PathVariable Long id) {
        RegistroBusquedaActiva registro = service.obtenerRegistro(id);
        if (registro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(registro);
    }

    @GetMapping(value = "/registros/{id}/reporte", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> descargarReporte(@PathVariable Long id) {
        RegistroBusquedaActiva registro = service.obtenerRegistro(id);
        if (registro == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayInputStream bis = reporteService.generarReportePdf(registro);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte-busqueda-" + registro.getNumeroRegistro() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @PostMapping("/registros")
    public ResponseEntity<RegistroBusquedaActiva> crearRegistro(@RequestBody RegistroBusquedaActiva registro) {
        RegistroBusquedaActiva creado = service.guardarRegistro(registro);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/registros/{id}")
    public ResponseEntity<RegistroBusquedaActiva> actualizarRegistro(
            @PathVariable Long id, @RequestBody RegistroBusquedaActiva registro) {
        RegistroBusquedaActiva existente = service.obtenerRegistro(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        registro.setId(id);
        return ResponseEntity.ok(service.guardarRegistro(registro));
    }
}
