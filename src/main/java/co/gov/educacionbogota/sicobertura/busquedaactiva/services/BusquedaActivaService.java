package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;

import java.util.List;

public interface BusquedaActivaService {
    
    /**
     * Consulta registros opcionalmente por estado y etapa
     */
    List<RegistroBusquedaActiva> consultarRegistros(String estado, Integer etapa);
    
    /**
     * Obtiene un registro puntual
     */
    RegistroBusquedaActiva obtenerRegistro(Long id);

    /**
     * Guarda y avanza la etapa de un registro
     */
    RegistroBusquedaActiva guardarRegistro(RegistroBusquedaActiva registro);
}
