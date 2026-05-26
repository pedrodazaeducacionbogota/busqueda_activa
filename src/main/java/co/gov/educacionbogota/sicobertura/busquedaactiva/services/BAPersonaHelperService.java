package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.repository.PersonaRepository;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

/**
 * Upsert PersonaEntity por (tipoDocumento, numeroDocumento). Reutilizado en
 * secciones 2, 5, 6 del wizard BA (atiende visita, acudiente, estudiante).
 *
 * Si la persona ya existe → reutiliza entity (preserva FK desde otros formularios).
 * Si no existe → crea nueva con tipoDoc resuelto vía RefListado.
 */
@Service
public class BAPersonaHelperService {

    @Autowired private PersonaRepository personaRepository;
    @Autowired private RefListadoRepository refListadoRepository;

    /**
     * @param toUpdate persona existente (null o entity vacía si se crea nueva)
     * @param codigoTipoDocumento código RefListado descripcion=TIPOS_DOCUMENTO
     */
    public PersonaEntity upsert(PersonaEntity toUpdate, String codigoTipoDocumento, String numeroDocumento,
                                 String primerNombre, String segundoNombre,
                                 String primerApellido, String segundoApellido,
                                 String celulares, String emails) {
        RefListado tipoDoc = refListadoRepository
                .findByCodigoAndDescripcionAndActivo(codigoTipoDocumento, "TIPOS_DOCUMENTO", 1)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo documento no encontrado: " + codigoTipoDocumento));

        // Si persona con mismo (tipoDoc, numDoc) ya existe en BD, reusarla
        Optional<PersonaEntity> existente = personaRepository
                .findByTipoDocumento_IdRefListadoAndNumeroDocumento(tipoDoc.getIdRefListado(), numeroDocumento);

        PersonaEntity persona;
        if (existente.isPresent()) {
            persona = existente.get();
        } else if (toUpdate != null && toUpdate.getId() != null) {
            persona = toUpdate;
            persona.setTipoDocumento(tipoDoc);
            persona.setNumeroDocumento(numeroDocumento);
        } else {
            persona = new PersonaEntity();
            persona.setTipoDocumento(tipoDoc);
            persona.setNumeroDocumento(numeroDocumento);
        }

        persona.setPrimerNombre(primerNombre);
        persona.setPrimerApellido(primerApellido);
        if (segundoNombre != null && !segundoNombre.isEmpty()) persona.setSegundoNombre(segundoNombre);
        if (segundoApellido != null && !segundoApellido.isEmpty()) persona.setSegundoApellido(segundoApellido);
        if (celulares != null) persona.setCelulares(celulares);
        if (emails != null) persona.setEmails(emails);

        return personaRepository.save(persona);
    }
}
