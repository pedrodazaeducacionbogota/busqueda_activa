package co.gov.educacionbogota.sicobertura.busquedaactiva.controllers;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.services.BusquedaActivaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busqueda-activa")
public class BusquedaActivaController {

    @Autowired
    private BusquedaActivaService service;

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
