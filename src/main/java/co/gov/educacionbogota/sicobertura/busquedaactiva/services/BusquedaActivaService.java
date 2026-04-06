package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;

import java.util.List;

public interface BusquedaActivaService {
    
    @org.springframework.lang.NonNull
    List<RegistroBusquedaActiva> consultarRegistros(String estado, Integer etapa);
    
    @org.springframework.lang.Nullable
    RegistroBusquedaActiva obtenerRegistro(@org.springframework.lang.NonNull Long id);

    @org.springframework.lang.NonNull
    RegistroBusquedaActiva guardarRegistro(@org.springframework.lang.NonNull RegistroBusquedaActiva registro);
}
