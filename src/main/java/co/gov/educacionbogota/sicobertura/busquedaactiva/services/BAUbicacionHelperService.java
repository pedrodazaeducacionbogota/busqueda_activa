package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.UbicacionRepository;

/**
 * Upsert UbicacionEntity con localidad obligatoria + barrio opcional.
 * Si codigoBarrio es null/empty/"0" → guarda barrioOtro como string libre.
 *
 * Reutilizado en sección 1 (ubicación de la visita) y sección 2 (residencia del atiende).
 */
@Service
public class BAUbicacionHelperService {

    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private RefListadoRepository refListadoRepository;

    public UbicacionEntity upsert(UbicacionEntity toUpdate, String codigoLocalidad,
                                   String codigoBarrio, String barrioOtroFallback) {
        RefListado localidad = refListadoRepository
                .findByCodigoAndDescripcionAndActivo(codigoLocalidad, "LOCALIDADES", 1)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Localidad no encontrada: " + codigoLocalidad));

        UbicacionEntity ubicacion = (toUpdate != null && toUpdate.getId() != null)
                ? toUpdate : new UbicacionEntity();
        ubicacion.setLocalidad(localidad);

        if (codigoBarrio != null && !codigoBarrio.isEmpty() && !"0".equals(codigoBarrio)) {
            RefListado barrio = refListadoRepository
                    .findByCodigoAndDescripcionAndActivo(codigoBarrio, "BARRIOS", 1)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Barrio no encontrado: " + codigoBarrio));
            ubicacion.setBarrio(barrio);
            ubicacion.setBarrioOtro(null);
        } else {
            ubicacion.setBarrio(null);
            ubicacion.setBarrioOtro(barrioOtroFallback);
        }

        return ubicacionRepository.save(ubicacion);
    }

    /** Actualiza dirección estructurada (tipo vía + dirección armada + complemento + estrato) sin tocar localidad/barrio. */
    public UbicacionEntity setDireccion(UbicacionEntity ubicacion, String codigoTipoVia, String direccion,
                                         String direccionComplemento, Integer estrato) {
        if (codigoTipoVia != null) ubicacion.setTipoDireccion(codigoTipoVia);
        if (direccion != null) ubicacion.setDireccion(direccion);
        if (direccionComplemento != null) ubicacion.setDireccionComplemento(direccionComplemento);
        if (estrato != null) ubicacion.setEstrato(estrato);
        return ubicacionRepository.save(ubicacion);
    }
}
