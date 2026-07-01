package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.repository.UbicacionRepository;

/**
 * Upsert UbicacionEntity. localidad obligatoria, barrio opcional.
 * Si barrio es OTRO o null → guarda barrioOtro como string libre.
 */
@Service
public class BAUbicacionHelperService {

    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private BARefResolverService resolver;

    public UbicacionEntity upsert(UbicacionEntity toUpdate, Long codigoLocalidad,
                                   Long codigoBarrio, String barrioOtroFallback) {
        RefListado localidadRef = resolver.resolveRequired(codigoLocalidad, "LOCALIDAD");

        UbicacionEntity ubicacion = (toUpdate != null && toUpdate.getId() != null)
                ? toUpdate : new UbicacionEntity();
        ubicacion.setLocalidad(localidadRef);

        if (codigoBarrio != null) {
            RefListado barrioRef = resolver.resolveRequired(codigoBarrio, "BARRIOS");
            if ("OTRO".equals(barrioRef.getCodigo())) {
                ubicacion.setBarrio(null);
                ubicacion.setBarrioOtro(barrioOtroFallback);
            } else {
                ubicacion.setBarrio(barrioRef);
                ubicacion.setBarrioOtro(null);
            }
        } else {
            ubicacion.setBarrio(null);
            ubicacion.setBarrioOtro(barrioOtroFallback);
        }

        return ubicacionRepository.save(ubicacion);
    }

    public UbicacionEntity setDireccion(UbicacionEntity ubicacion, Long codigoTipoVia,
                                         String direccion, String direccionComplemento, Integer estrato) {
        return setDireccion(ubicacion, codigoTipoVia, null, null, null, null, null,
                direccion, direccionComplemento, estrato);
    }

    public UbicacionEntity setDireccion(UbicacionEntity ubicacion, Long codigoTipoVia,
                                         String numeroVia, String letraVia, String sufijoVia,
                                         String numeroSecVia, String numeroFinVia,
                                         String direccion, String direccionComplemento, Integer estrato) {
        if (codigoTipoVia != null) {
            RefListado tipoViaRef = resolver.resolveRequired(codigoTipoVia, "TIPOS_VIA");
            ubicacion.setTipoVia(tipoViaRef.getCodigo());
        }
        ubicacion.setNumeroVia(numeroVia);
        ubicacion.setLetraVia(letraVia);
        ubicacion.setSufijoVia(sufijoVia);
        ubicacion.setNumeroSecVia(numeroSecVia);
        ubicacion.setNumeroFinVia(numeroFinVia);
        if (direccion != null) ubicacion.setDireccion(direccion);
        if (direccionComplemento != null) ubicacion.setDireccionComplemento(direccionComplemento);
        if (estrato != null) ubicacion.setEstrato(estrato);
        return ubicacionRepository.save(ubicacion);
    }
}
