package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.repository.PersonaRepository;

/**
 * Upsert PersonaEntity por (tipoDocumento, numeroDocumento). Reutilizado en
 * estudiante, acudiente, hermano.
 */
@Service
public class BAPersonaHelperService {

    @Autowired private PersonaRepository personaRepository;
    @Autowired private BARefResolverService resolver;

    public PersonaEntity upsert(PersonaEntity toUpdate, Long codigoTipoDocumento, String numeroDocumento,
                                 String primerNombre, String segundoNombre,
                                 String primerApellido, String segundoApellido,
                                 String celulares, String emails) {
        RefListado tipoDoc = resolver.resolveRequired(codigoTipoDocumento, "TIPOS_DOCUMENTO");

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
