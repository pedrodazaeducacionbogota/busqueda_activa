package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa1Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa2Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa3Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.BAEtapa4Dto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ResponseBASeccionesDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BANoEstudiandoEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudColegioRepository;

/**
 * Detalle de cada etapa como DTO. Reverse mapping entity → dto para repintar el wizard
 * al retomar un formulario. Switch 0-4 desde el controller.
 */
@Service
public class BAGetService {

    @Autowired private BAFormularioService formularioService;
    @Autowired private RefListadoRepository refRepo;
    @Autowired private SolicitudColegioRepository solicitudColegioRepository;

    @Transactional(readOnly = true)
    public ResponseBASeccionesDto detalleEtapa0(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        return new ResponseBASeccionesDto(f.getId(),
                f.getProfesional() != null ? f.getProfesional().getId() : null);
    }

    @Transactional(readOnly = true)
    public BAEtapa1Dto detalleEtapa1(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BAEtapa1Dto dto = new BAEtapa1Dto();
        if (f.getEstudiante() == null || f.getEstudiante().getPersona() == null) return dto;

        PersonaEntity p = f.getEstudiante().getPersona();
        if (p.getTipoDocumento() != null) dto.setCodigoTipoDocumento(p.getTipoDocumento().getCodigo());
        dto.setNumeroDocumento(p.getNumeroDocumento());
        dto.setPrimerNombre(p.getPrimerNombre());
        dto.setSegundoNombre(p.getSegundoNombre());
        dto.setPrimerApellido(p.getPrimerApellido());
        dto.setSegundoApellido(p.getSegundoApellido());
        if (p.getPaisNacimiento() != null) dto.setCodigoPaisNacimiento(p.getPaisNacimiento().getCodigo());
        dto.setFechaNacimiento(p.getFechaNacimientoStr());
        if (p.getSexo() != null) dto.setCodigoSexo(p.getSexo().getCodigo());
        if (p.getEtnia() != null) dto.setCodigoEtnia(p.getEtnia().getCodigo());
        dto.setEtniaOtro(p.getEtniaOtro());
        dto.setDiscapacidad(Boolean.TRUE.equals(p.getDiscapacidad()));
        if (p.getTipoDiscapacidad() != null) dto.setCodigoTipoDiscapacidad(p.getTipoDiscapacidad().getCodigo());
        dto.setCertDiscapacidad(Boolean.TRUE.equals(p.getCertDiscapacidad()));
        dto.setSoporteDiscapacidad(p.getSoporteDiscapacidad());
        if (p.getPoblacionDiferencial() != null) dto.setCodigoPoblacionDiferencial(p.getPoblacionDiferencial().getCodigo());
        dto.setPoblacionOtro(p.getPoblacionOtro());
        dto.setGestante(p.isGestante());
        dto.setCorreo(p.getEmails());
        dto.setCelular(p.getCelulares());
        // Inferido: mayor de edad a nombre propio si registró contacto propio
        dto.setMayorEdadNombrePropio(p.getEmails() != null || p.getCelulares() != null);
        mapUbicacion(p.getUbicacion(), dto);
        return dto;
    }

    private void mapUbicacion(UbicacionEntity u, BAEtapa1Dto dto) {
        if (u == null) return;
        if (u.getLocalidad() != null) dto.setCodigoLocalidad(u.getLocalidad().getCodigo());
        dto.setCodigoBarrio(u.getBarrio() != null ? u.getBarrio().getCodigo() : "0");
        dto.setBarrioOtro(u.getBarrioOtro());
        dto.setCodigoTipoVia(u.getTipoDireccion());
        dto.setDireccion(u.getDireccion());
        dto.setDireccionComplemento(u.getDireccionComplemento());
    }

    @Transactional(readOnly = true)
    public BAEtapa2Dto detalleEtapa2(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BAEtapa2Dto dto = new BAEtapa2Dto();
        SolicitudEntity s = f.getSolicitud();
        if (s != null) {
            if (s.getUltimoAnioAprobado() != null) dto.setCodigoUltimoAnioAprobado(s.getUltimoAnioAprobado().getCodigo());
            if (s.getGradoSolicitaCupo() != null) dto.setCodigoGradoSolicitaCupo(s.getGradoSolicitaCupo().getCodigo());
            dto.setTieneHermano(s.isTieneHermano());
            PersonaEntity h = s.getHermano();
            if (h != null) {
                if (h.getTipoDocumento() != null) dto.setCodigoTipoDocumentoHermano(h.getTipoDocumento().getCodigo());
                dto.setNumeroDocumentoHermano(h.getNumeroDocumento());
                dto.setPrimerNombreHermano(h.getPrimerNombre());
                dto.setSegundoNombreHermano(h.getSegundoNombre());
                dto.setPrimerApellidoHermano(h.getPrimerApellido());
                dto.setSegundoApellidoHermano(h.getSegundoApellido());
            }
            List<Long> idsColegios = solicitudColegioRepository
                    .findBySolicitud_IdOrderByOrdenPreferencia(s.getId()).stream()
                    .map(sc -> sc.getColegio() != null ? sc.getColegio().getId() : null)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            dto.setIdsColegios(idsColegios);
        }
        dto.setMismaInstitucionHermano(f.isMismaInstitucionHermano());
        if (f.getInstitucionHermano() != null) dto.setIdInstitucionHermano(f.getInstitucionHermano().getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public BAEtapa3Dto detalleEtapa3(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BAEtapa3Dto dto = new BAEtapa3Dto();
        PersonaEntity p = f.getAcudiente();
        if (p == null) return dto;

        if (p.getTipoDocumento() != null) dto.setCodigoTipoDocumento(p.getTipoDocumento().getCodigo());
        dto.setNumeroDocumento(p.getNumeroDocumento());
        dto.setPrimerNombre(p.getPrimerNombre());
        dto.setSegundoNombre(p.getSegundoNombre());
        dto.setPrimerApellido(p.getPrimerApellido());
        dto.setSegundoApellido(p.getSegundoApellido());
        dto.setCorreo(p.getEmails());
        dto.setCelular(p.getCelulares());
        dto.setParentescoOtro(p.getParentescoOtro());
        if (p.getIdParentesco() != null) {
            refRepo.findById(p.getIdParentesco().longValue()).ifPresent(r -> dto.setCodigoParentesco(r.getCodigo()));
        }
        if (p.getIdNvlEscolaridad() != null) {
            refRepo.findById(p.getIdNvlEscolaridad().longValue()).ifPresent(r -> dto.setCodigoNivelEscolaridad(r.getCodigo()));
        }
        if (p.getIdOcupacion() != null) {
            refRepo.findById(p.getIdOcupacion().longValue()).ifPresent(r -> dto.setCodigoOcupacion(r.getCodigo()));
        }
        UbicacionEntity u = p.getUbicacion();
        if (u != null) {
            if (u.getLocalidad() != null) dto.setCodigoLocalidad(u.getLocalidad().getCodigo());
            dto.setCodigoBarrio(u.getBarrio() != null ? u.getBarrio().getCodigo() : "0");
            dto.setBarrioOtro(u.getBarrioOtro());
            dto.setCodigoTipoVia(u.getTipoDireccion());
            dto.setDireccion(u.getDireccion());
            dto.setDireccionComplemento(u.getDireccionComplemento());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public BAEtapa4Dto detalleEtapa4(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BAEtapa4Dto dto = new BAEtapa4Dto();
        List<BAEtapa4Dto.NoEstudiandoItem> items = new ArrayList<>();
        for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
            BAEtapa4Dto.NoEstudiandoItem item = new BAEtapa4Dto.NoEstudiandoItem();
            item.setRangoEdadCodigo(r.getRangoEdadCodigo());
            item.setCuantos(r.getCuantos());
            if (r.getRazon() != null) item.setCodigoRazon(r.getRazon().getCodigo());
            if (r.getRazonOtra() != null) item.setCodigoRazonOtra(r.getRazonOtra().getCodigo());
            items.add(item);
        }
        dto.setExistenNoEstudiando(!items.isEmpty());
        dto.setRangos(items);
        return dto;
    }
}
