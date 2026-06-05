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
import co.gov.educacionbogota.sicobertura.dto.RefListadoKVDto;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudColegioRepository;

/**
 * Detalle de cada etapa como DTO. Reverse mapping entity → dto para repintar el wizard.
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
        dto.setTipoDocumento(toKV(p.getTipoDocumento()));
        dto.setNumeroDocumento(p.getNumeroDocumento());
        dto.setPrimerNombre(p.getPrimerNombre());
        dto.setSegundoNombre(p.getSegundoNombre());
        dto.setPrimerApellido(p.getPrimerApellido());
        dto.setSegundoApellido(p.getSegundoApellido());
        dto.setPaisNacimiento(toKV(p.getPaisNacimiento()));
        dto.setFechaNacimiento(p.getFechaNacimientoStr());
        dto.setSexo(toKV(p.getSexo()));
        dto.setEtnia(toKV(p.getEtnia()));
        dto.setEtniaOtro(p.getEtniaOtro());
        dto.setDiscapacidad(Boolean.TRUE.equals(p.getDiscapacidad()));
        dto.setTipoDiscapacidad(toKV(p.getTipoDiscapacidad()));
        dto.setCertDiscapacidad(Boolean.TRUE.equals(p.getCertDiscapacidad()));
        dto.setSoporteDiscapacidad(p.getSoporteDiscapacidad());
        dto.setPoblacionDiferencial(toKV(p.getPoblacionDiferencial()));
        dto.setPoblacionOtro(p.getPoblacionOtro());
        dto.setGestante(p.isGestante());
        dto.setCorreo(p.getEmails());
        dto.setCelular(p.getCelulares());
        dto.setMayorEdadNombrePropio(p.getEmails() != null || p.getCelulares() != null);
        mapUbicacion(p.getUbicacion(), dto);
        return dto;
    }

    private void mapUbicacion(UbicacionEntity u, BAEtapa1Dto dto) {
        if (u == null) return;
        dto.setLocalidad(toKV(u.getLocalidad()));
        dto.setBarrio(u.getBarrio() != null ? toKV(u.getBarrio())
                : RefListadoKVDto.builder().codigo("0").build());
        dto.setBarrioOtro(u.getBarrioOtro());
        dto.setTipoVia(u.getTipoDireccion() != null
                ? RefListadoKVDto.builder().codigo(u.getTipoDireccion()).build() : null);
        dto.setDireccion(u.getDireccion());
        dto.setDireccionComplemento(u.getDireccionComplemento());
    }

    @Transactional(readOnly = true)
    public BAEtapa2Dto detalleEtapa2(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        BAEtapa2Dto dto = new BAEtapa2Dto();
        SolicitudEntity s = f.getSolicitud();
        if (s != null) {
            dto.setUltimoAnioAprobado(toKV(s.getUltimoAnioAprobado()));
            dto.setGradoSolicitaCupo(toKV(s.getGradoSolicitaCupo()));
            dto.setTieneHermano(s.isTieneHermano());
            PersonaEntity h = s.getHermano();
            if (h != null) {
                dto.setTipoDocumentoHermano(toKV(h.getTipoDocumento()));
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

        dto.setTipoDocumento(toKV(p.getTipoDocumento()));
        dto.setNumeroDocumento(p.getNumeroDocumento());
        dto.setPrimerNombre(p.getPrimerNombre());
        dto.setSegundoNombre(p.getSegundoNombre());
        dto.setPrimerApellido(p.getPrimerApellido());
        dto.setSegundoApellido(p.getSegundoApellido());
        dto.setCorreo(p.getEmails());
        dto.setCelular(p.getCelulares());
        dto.setParentescoOtro(p.getParentescoOtro());
        if (p.getIdParentesco() != null) {
            refRepo.findById(p.getIdParentesco().longValue()).ifPresent(r -> dto.setParentesco(toKV(r)));
        }
        if (p.getIdNvlEscolaridad() != null) {
            refRepo.findById(p.getIdNvlEscolaridad().longValue()).ifPresent(r -> dto.setNivelEscolaridad(toKV(r)));
        }
        if (p.getIdOcupacion() != null) {
            refRepo.findById(p.getIdOcupacion().longValue()).ifPresent(r -> dto.setOcupacion(toKV(r)));
        }
        UbicacionEntity u = p.getUbicacion();
        if (u != null) {
            dto.setLocalidad(toKV(u.getLocalidad()));
            dto.setBarrio(u.getBarrio() != null ? toKV(u.getBarrio())
                    : RefListadoKVDto.builder().codigo("0").build());
            dto.setBarrioOtro(u.getBarrioOtro());
            dto.setTipoVia(u.getTipoDireccion() != null
                    ? RefListadoKVDto.builder().codigo(u.getTipoDireccion()).build() : null);
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
            item.setRazon(toKV(r.getRazon()));
            item.setRazonOtra(toKV(r.getRazonOtra()));
            items.add(item);
        }
        dto.setExistenNoEstudiando(!items.isEmpty());
        dto.setRangos(items);
        return dto;
    }

    private RefListadoKVDto toKV(RefListado r) {
        if (r == null) return null;
        return RefListadoKVDto.builder()
                .id(r.getIdRefListado())
                .codigo(r.getCodigo())
                .valorTxt(r.getValorTxt())
                .valorInt(r.getValorInt())
                .build();
    }
}
