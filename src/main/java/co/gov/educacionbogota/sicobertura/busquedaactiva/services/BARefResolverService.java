package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

/**
 * Resolver de ref_listado por id (idRefListado). Valida que la descripción
 * esperada coincida con la del registro encontrado.
 */
@Service
public class BARefResolverService {

    @Autowired
    private RefListadoRepository refRepo;

    /** Resuelve ref_listado por id; obligatorio. */
    public RefListado resolveRequired(Long id, String descripcion) {
        if (id == null) {
            throw new ReglaNegocioException("Campo ref_listado requerido para: " + descripcion);
        }
        return doResolve(id, descripcion);
    }

    /** Resuelve ref_listado por id; opcional (retorna null si id es null). */
    public RefListado resolveOptional(Long id, String descripcion) {
        return id == null ? null : doResolve(id, descripcion);
    }

    private RefListado doResolve(Long id, String descripcion) {
        RefListado ref = refRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ref no encontrada: id=" + id + " (descripcion esperada=" + descripcion + ")"));
        if (descripcion != null && !descripcion.equals(ref.getDescripcion())) {
            throw new ReglaNegocioException(
                    "Ref id=" + id + " descripcion=" + ref.getDescripcion()
                    + " no coincide con esperada=" + descripcion);
        }
        if (ref.getActivo() == null || ref.getActivo() != 1) {
            throw new ReglaNegocioException("Ref inactiva: id=" + id);
        }
        return ref;
    }
}
