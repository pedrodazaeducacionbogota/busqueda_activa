package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.RegistroBusquedaActivaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class BusquedaActivaServiceImpl implements BusquedaActivaService {

    @Autowired
    private RegistroBusquedaActivaRepository repository;

    @Override
    @org.springframework.lang.NonNull
    public List<RegistroBusquedaActiva> consultarRegistros(String estado, Integer etapa) {
        if (estado != null && !estado.isEmpty() && etapa != null) {
            return repository.findByEstadoAndUltimaEtapaRegistrada(estado, etapa);
        } else if (estado != null && !estado.isEmpty()) {
            return repository.findByEstado(estado);
        } else if (etapa != null) {
            return repository.findByUltimaEtapaRegistrada(etapa);
        }
        return repository.findAll();
    }

    @Override
    @org.springframework.lang.Nullable
    public RegistroBusquedaActiva obtenerRegistro(@org.springframework.lang.NonNull Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @org.springframework.lang.NonNull
    public RegistroBusquedaActiva guardarRegistro(@org.springframework.lang.NonNull RegistroBusquedaActiva registro) {
        if (registro.getId() == null) {
            registro.setFechaRegistro(new Date());
            registro.setNumeroRegistro(generarNumeroRegistro());
        }
        
        // Logica para determinar la ultima etapa basandose en que los objetos no sean nulos
        determinarEtapa(registro);
        
        return repository.save(registro);
    }
    
    private void determinarEtapa(RegistroBusquedaActiva registro) {
        if (registro.getEstudiante() != null) {
            registro.setUltimaEtapaRegistrada(1);
        }
        if (registro.getSolicitudCupo() != null) {
            registro.setUltimaEtapaRegistrada(2);
        }
        if (registro.getResponsable() != null) {
            registro.setUltimaEtapaRegistrada(3);
        }
        if (registro.getTieneNinosSinEstudio() != null) {
            // Incluso si es false, se considera completada la etapa de factores (Etapa 4)
            registro.setUltimaEtapaRegistrada(4);
            
            // Asegurar que cada factor tenga la referencia al registro
            if (registro.getFactoresDesescolarizacion() != null) {
                registro.getFactoresDesescolarizacion().forEach(f -> f.setRegistro(registro));
            }
        }
        
        // Determinar finalizacion
        if (registro.getUltimaEtapaRegistrada() != null && registro.getUltimaEtapaRegistrada() >= 4) {
            registro.setEstado("Finalizado");
        } else {
            registro.setEstado("Pendiente");
        }
    }

    private String generarNumeroRegistro() {
        return "BA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
