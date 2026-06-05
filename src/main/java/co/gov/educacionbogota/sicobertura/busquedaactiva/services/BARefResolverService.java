package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

/**
 * Resuelve RefListadoKVDto → RefListado aceptando cualquier campo presente.
 * Prioridad: id → valorTxt → valorInt → codigo.
 */
@Service
public class BARefResolverService {

    @Autowired
    private RefListadoRepository refRepo;

    public RefListado resolve(RefListadoKVDto kv, String descripcion) {
        if (kv == null) {
            throw new ReglaNegocioException("Campo ref_listado requerido para: " + descripcion);
        }

        if (kv.getId() != null) {
            return refRepo.findById(kv.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Ref no encontrada: id=" + kv.getId() + " descripcion=" + descripcion));
        }

        if (kv.getValorTxt() != null && !kv.getValorTxt().isEmpty()) {
            return refRepo.findFirstByValorTxtAndDescripcionAndActivo(kv.getValorTxt(), descripcion, 1)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Ref no encontrada: valorTxt=" + kv.getValorTxt() + " descripcion=" + descripcion));
        }

        if (kv.getValorInt() != null) {
            return refRepo.findFirstByValorIntAndDescripcionAndActivo(kv.getValorInt(), descripcion, 1)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Ref no encontrada: valorInt=" + kv.getValorInt() + " descripcion=" + descripcion));
        }

        throw new ReglaNegocioException(
                "RefListadoKVDto sin campo resoluble (id, valorTxt o valorInt) para: " + descripcion);
    }
}
