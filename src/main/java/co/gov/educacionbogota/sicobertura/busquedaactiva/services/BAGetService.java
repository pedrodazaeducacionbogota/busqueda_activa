package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion1Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion2Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion3Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion4Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion5Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion6Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BASeccion7Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ResponseBASeccionesDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BANoEstudiandoEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

/**
 * Detalle de cada sección como DTO. Reverse mapping de entity → dto para que front
 * pueda pintar formulario step en wizard al retomar formulario.
 */
@Service
public class BAGetService {

    @Autowired private BAFormularioService formularioService;
    @Autowired private RefListadoRepository refRepo;

    @Transactional(readOnly = true)
    public ResponseBASeccionesDto detalleSeccion0(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        return new ResponseBASeccionesDto(f.getId(),
                f.getProfesional() != null ? f.getProfesional().getId() : null);
    }

    @Transactional(readOnly = true)
    public BASeccion1Dto detalleSeccion1(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion1Dto dto = new BASeccion1Dto();
        if (f.getActividad() != null) dto.setCodigoActividad(f.getActividad().getCodigo());
        dto.setActividadOtra(f.getActividadOtra());
        dto.setNombreEventoFeria(f.getNombreEventoFeria());
        if (f.getPoblacionEvento() != null && !f.getPoblacionEvento().isEmpty()) {
            dto.setPoblacionEvento(Arrays.asList(f.getPoblacionEvento().split(",")));
        }
        if (f.getUbicacion() != null) {
            dto.setBarrioOtro(f.getUbicacion().getBarrioOtro());
            if (f.getUbicacion().getLocalidad() != null) dto.setCodigoLocalidad(f.getUbicacion().getLocalidad().getCodigo());
            dto.setCodigoBarrio(f.getUbicacion().getBarrio() != null ? f.getUbicacion().getBarrio().getCodigo() : "0");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion2Dto detalleSeccion2(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion2Dto dto = new BASeccion2Dto();
        PersonaEntity p = f.getAtiendeVisita();
        if (p != null) {
            dto.setIdPersona(p.getId());
            dto.setNumeroDocumento(p.getNumeroDocumento());
            dto.setPrimerNombre(p.getPrimerNombre());
            dto.setSegundoNombre(p.getSegundoNombre());
            dto.setPrimerApellido(p.getPrimerApellido());
            dto.setSegundoApellido(p.getSegundoApellido());
            dto.setCelulares(p.getCelulares());
            dto.setEmails(p.getEmails());
            if (p.getTipoDocumento() != null) dto.setCodigoTipoDocumento(p.getTipoDocumento().getCodigo());
            if (p.getIdCatSisben() != null) {
                refRepo.findById(p.getIdCatSisben().longValue())
                        .ifPresent(r -> dto.setCodigoCategoriaSisben(r.getCodigo()));
            }
            if (p.getUbicacion() != null) {
                dto.setBarrioOtro(p.getUbicacion().getBarrioOtro());
                dto.setDireccion(p.getUbicacion().getDireccion());
                dto.setDireccionComplemento(p.getUbicacion().getDireccionComplemento());
                dto.setEstrato(p.getUbicacion().getEstrato());
                if (p.getUbicacion().getLocalidad() != null) dto.setCodigoLocalidad(p.getUbicacion().getLocalidad().getCodigo());
                dto.setCodigoBarrio(p.getUbicacion().getBarrio() != null ? p.getUbicacion().getBarrio().getCodigo() : "0");
            }
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion3Dto detalleSeccion3(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion3Dto dto = new BASeccion3Dto();
        dto.setColegiosCerca(f.isColegiosCerca());
        dto.setColegioCercaCual(f.getColegioCercaCual());
        dto.setJardinesCerca(f.isJardinesCerca());
        dto.setJardinCercaCual(f.getJardinCercaCual());
        if (f.getIdeColegioCerca() != null) dto.setIdeColegioCerca(f.getIdeColegioCerca().getId());
        if (f.getIdeJardinCerca() != null) dto.setIdeJardinCerca(f.getIdeJardinCerca().getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion4Dto detalleSeccion4(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion4Dto dto = new BASeccion4Dto();
        List<BASeccion4Dto.NoEstudiandoItem> items = new ArrayList<>();
        for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
            BASeccion4Dto.NoEstudiandoItem item = new BASeccion4Dto.NoEstudiandoItem();
            item.setRangoEdadCodigo(r.getRangoEdadCodigo());
            item.setCuantos(r.getCuantos());
            if (r.getRazon() != null) item.setCodigoRazon(r.getRazon().getCodigo());
            item.setRazonOtra(r.getRazonOtra());
            items.add(item);
        }
        dto.setRangos(items);
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion5Dto detalleSeccion5(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion5Dto dto = new BASeccion5Dto();
        dto.setAtiendeVisitaAcudiente(f.isAtiendeVisitaAcudiente());
        PersonaEntity p = f.getAcudiente();
        if (p == null) return dto;

        dto.setIdPersona(p.getId());
        dto.setFamiliarOtro(p.getParentescoOtro());

        if (p.getIdParentesco() != null) {
            refRepo.findById(p.getIdParentesco().longValue())
                    .ifPresent(r -> dto.setCodigoParentesco(r.getCodigo()));
        }
        if (p.getIdNvlEscolaridad() != null) {
            refRepo.findById(p.getIdNvlEscolaridad().longValue())
                    .ifPresent(r -> dto.setCodigoNivelEscolaridad(r.getCodigo()));
        }
        if (p.getIdOcupacion() != null) {
            refRepo.findById(p.getIdOcupacion().longValue())
                    .ifPresent(r -> dto.setCodigoOcupacion(r.getCodigo()));
        }
        // Si no es reuso del atiende, devolver también identificación personal
        if (!f.isAtiendeVisitaAcudiente()) {
            dto.setNumeroDocumento(p.getNumeroDocumento());
            dto.setPrimerNombre(p.getPrimerNombre());
            dto.setSegundoNombre(p.getSegundoNombre());
            dto.setPrimerApellido(p.getPrimerApellido());
            dto.setSegundoApellido(p.getSegundoApellido());
            dto.setCelulares(p.getCelulares());
            dto.setEmails(p.getEmails());
            if (p.getTipoDocumento() != null) dto.setCodigoTipoDocumento(p.getTipoDocumento().getCodigo());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion6Dto detalleSeccion6(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion6Dto dto = new BASeccion6Dto();
        if (f.getEstudiante() == null) return dto;

        PersonaEntity p = f.getEstudiante().getPersona();
        dto.setIdPersona(p.getId());
        dto.setNumeroDocumento(p.getNumeroDocumento());
        dto.setPrimerNombre(p.getPrimerNombre());
        dto.setSegundoNombre(p.getSegundoNombre());
        dto.setPrimerApellido(p.getPrimerApellido());
        dto.setSegundoApellido(p.getSegundoApellido());
        if (p.getTipoDocumento() != null) dto.setCodigoTipoDocumento(p.getTipoDocumento().getCodigo());
        if (p.getPaisNacimiento() != null) dto.setPaisNacimiento(p.getPaisNacimiento().getCodigo());
        if (p.getIdMunicipioExpDoc() != null) {
            refRepo.findById(p.getIdMunicipioExpDoc().longValue())
                    .ifPresent(r -> dto.setMunicipioExpDoc(r.getCodigo()));
        }
        if (p.getFechaNacimiento() != null) {
            dto.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").format(p.getFechaNacimiento()));
        }
        if (p.getSexo() != null) dto.setCodigoSexo(p.getSexo().getCodigo());
        if (p.getIdGenero() != null) {
            refRepo.findById(p.getIdGenero().longValue())
                    .ifPresent(r -> dto.setCodigoGenero(r.getCodigo()));
        }
        dto.setDiscapacidad(Boolean.TRUE.equals(p.getDiscapacidad()));
        if (p.getTipoDiscapacidad() != null) dto.setCodigoTipoDiscapacidad(p.getTipoDiscapacidad().getCodigo());
        dto.setCertDiscapacidad(Boolean.TRUE.equals(p.getCertDiscapacidad()));
        RefListado enfoque = f.getEstudiante().getEnfoqueDiferencial();
        if (enfoque != null) dto.setCodigoEnfoqueDiferencial(enfoque.getCodigo());
        return dto;
    }

    @Transactional(readOnly = true)
    public BASeccion7Dto detalleSeccion7(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BASeccion7Dto dto = new BASeccion7Dto();
        if (f.getUltimoAnioEstudio() != null) dto.setCodigoUltimoAnioEstudio(f.getUltimoAnioEstudio().getCodigo());
        dto.setRepitioUltimoAnio(f.isRepitioUltimoAnio());
        if (f.getVecesRepitioAnio() != null) dto.setCodigoVecesRepitio(f.getVecesRepitioAnio().getCodigo());
        if (f.getUltimoAnioAprobado() != null) dto.setCodigoUltimoAnioAprobado(f.getUltimoAnioAprobado().getCodigo());
        if (f.getIdeSolicitaCupo() != null) dto.setIdeSolicitaCupo(f.getIdeSolicitaCupo().getId());
        if (f.getGradoSolicitaCupo() != null) dto.setCodigoGradoSolicitaCupo(f.getGradoSolicitaCupo().getCodigo());
        return dto;
    }
}
