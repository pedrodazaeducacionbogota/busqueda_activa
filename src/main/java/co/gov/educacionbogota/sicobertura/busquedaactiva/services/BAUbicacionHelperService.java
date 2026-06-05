package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.repository.UbicacionRepository;

/**
 * Upsert UbicacionEntity. localidad obligatoria, barrio opcional.
 * Si barrio es null o no tiene campos resolubles → guarda barrioOtro como string libre.
 */
@Service
public class BAUbicacionHelperService {

    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private BARefResolverService resolver;

    public UbicacionEntity upsert(UbicacionEntity toUpdate, RefListadoKVDto localidad,
                                   RefListadoKVDto barrio, String barrioOtroFallback) {
        RefListado localidadRef = resolver.resolve(localidad, "LOCALIDADES");

        UbicacionEntity ubicacion = (toUpdate != null && toUpdate.getId() != null)
                ? toUpdate : new UbicacionEntity();
        ubicacion.setLocalidad(localidadRef);

        if (barrio != null && tieneValor(barrio)) {
            RefListado barrioRef = resolver.resolve(barrio, "BARRIOS");
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

    public UbicacionEntity setDireccion(UbicacionEntity ubicacion, RefListadoKVDto tipoVia,
                                         String direccion, String direccionComplemento, Integer estrato) {
        if (tipoVia != null && tieneValor(tipoVia)) {
            RefListado tipoViaRef = resolver.resolve(tipoVia, "TIPOS_VIA");
            ubicacion.setTipoDireccion(tipoViaRef.getCodigo());
        }
        if (direccion != null) ubicacion.setDireccion(direccion);
        if (direccionComplemento != null) ubicacion.setDireccionComplemento(direccionComplemento);
        if (estrato != null) ubicacion.setEstrato(estrato);
        return ubicacionRepository.save(ubicacion);
    }

    private boolean tieneValor(RefListadoKVDto kv) {
        return kv.getId() != null
                || (kv.getValorTxt() != null && !kv.getValorTxt().isEmpty())
                || kv.getValorInt() != null;
    }
}
