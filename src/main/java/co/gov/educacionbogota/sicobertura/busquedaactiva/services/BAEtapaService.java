package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import co.gov.educacionbogota.sicobertura.entities.EstudianteEntity;
import co.gov.educacionbogota.sicobertura.entities.IdeEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudColegioEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.EstudianteRepository;
import co.gov.educacionbogota.sicobertura.repository.IdeRepository;
import co.gov.educacionbogota.sicobertura.repository.PersonaRepository;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudColegioRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudRepository;

@Service
public class BAEtapaService {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_COLEGIOS = 10;

    @Autowired private BAFormularioService formularioService;
    @Autowired private BAPersonaHelperService personaHelper;
    @Autowired private BAUbicacionHelperService ubicacionHelper;
    @Autowired private BARefResolverService resolver;
    @Autowired private RefListadoRepository refRepo;
    @Autowired private PersonaRepository personaRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private SolicitudColegioRepository solicitudColegioRepository;
    @Autowired private IdeRepository ideRepository;

    // ==================== ETAPA 1 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa1(Long id, BAEtapa1Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        EstudianteEntity estudiante = f.getEstudiante();
        PersonaEntity persona = (estudiante != null) ? estudiante.getPersona() : null;

        String celular = dto.isMayorEdadNombrePropio() ? dto.getCelular() : null;
        String correo  = dto.isMayorEdadNombrePropio() ? dto.getCorreo()  : null;
        persona = personaHelper.upsert(persona,
                dto.getTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                celular, correo);

        persona.setPaisNacimiento(lookupRef(dto.getPaisNacimiento(), "PAIS"));
        try {
            persona.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getFechaNacimiento()));
        } catch (java.text.ParseException ex) {
            throw new ReglaNegocioException("Fecha nacimiento inválida: " + dto.getFechaNacimiento());
        }
        persona.setFechaNacimientoStr(dto.getFechaNacimiento());
        persona.setSexo(lookupRef(dto.getSexo(), "SEXOS"));

        RefListado etnia = lookupRef(dto.getEtnia(), "ETNIAS");
        persona.setEtnia(etnia);
        persona.setEtniaOtro("OTRO".equals(etnia.getCodigo()) ? nullIfEmpty(dto.getEtniaOtro()) : null);

        persona.setDiscapacidad(dto.isDiscapacidad());
        if (dto.isDiscapacidad()) {
            persona.setTipoDiscapacidad(lookupRef(dto.getTipoDiscapacidad(), "TIPOS_DISCAPACIDAD"));
            persona.setCertDiscapacidad(dto.isCertDiscapacidad());
            persona.setSoporteDiscapacidad(nullIfEmpty(dto.getSoporteDiscapacidad()));
        } else {
            persona.setTipoDiscapacidad(null);
            persona.setCertDiscapacidad(false);
            persona.setSoporteDiscapacidad(null);
        }

        if (dto.getPoblacionDiferencial() != null) {
            RefListado poblacion = lookupRef(dto.getPoblacionDiferencial(), "POBLACION_EVENT_BA");
            persona.setPoblacionDiferencial(poblacion);
            persona.setPoblacionOtro("OTRO".equals(poblacion.getCodigo()) ? nullIfEmpty(dto.getPoblacionOtro()) : null);
        }
        persona.setGestante(dto.isGestante());

        UbicacionEntity residencia = ubicacionHelper.upsert(persona.getUbicacion(),
                dto.getLocalidad(), dto.getBarrio(), dto.getBarrioOtro());
        residencia = ubicacionHelper.setDireccion(residencia, dto.getTipoVia(),
                dto.getDireccion(), dto.getDireccionComplemento(), null);
        persona.setUbicacion(residencia);
        persona = personaRepository.save(persona);

        if (estudiante == null) estudiante = new EstudianteEntity();
        estudiante.setPersona(persona);

        LocalDate fechaNac = LocalDate.parse(dto.getFechaNacimiento(), DT_FORMATTER);
        Period periodo = Period.between(fechaNac, LocalDate.now());
        estudiante.setEdadInt(periodo.getYears());
        estudiante.setEdadTxt(periodo.getYears() + " años, " + periodo.getMonths()
                + " meses, " + periodo.getDays() + " días.");
        estudiante.setRangoEdad(resolverRangoEdad(periodo.getYears()));
        estudiante = estudianteRepository.save(estudiante);

        f.setEstudiante(estudiante);
        f.setUltimaEtapa(1);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== ETAPA 2 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa2(Long id, BAEtapa2Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        if (f.getEstudiante() == null || f.getEstudiante().getPersona() == null) {
            throw new ReglaNegocioException("Etapa 1 (estudiante) debe completarse antes de la etapa 2");
        }
        if (dto.getIdsColegios() == null || dto.getIdsColegios().isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar al menos una institución");
        }
        if (dto.getIdsColegios().size() > MAX_COLEGIOS) {
            throw new ReglaNegocioException("Máximo " + MAX_COLEGIOS + " instituciones en orden de preferencia");
        }

        SolicitudEntity sol = f.getSolicitud();
        if (sol == null) {
            sol = new SolicitudEntity();
            sol.setUuid(UUID.randomUUID().toString());
            sol.setFechaCrea(new Date());
        }
        sol.setSolicitante(f.getEstudiante().getPersona());
        sol.setVigencia(f.getVigencia());
        sol.setEtapa(f.getEtapa());
        sol.setAceptaPoliticas(true);
        sol.setEditable(true);
        sol.setUltimoAnioAprobado(lookupRef(dto.getUltimoAnioAprobado(), "GRADOS_ESCOLARES"));
        sol.setGradoSolicitaCupo(lookupRef(dto.getGradoSolicitaCupo(), "GRADOS_ESCOLARES"));

        sol.setTieneHermano(dto.isTieneHermano());
        if (dto.isTieneHermano()) {
            PersonaEntity hermano = personaHelper.upsert(sol.getHermano(),
                    dto.getTipoDocumentoHermano(), dto.getNumeroDocumentoHermano(),
                    dto.getPrimerNombreHermano(), dto.getSegundoNombreHermano(),
                    dto.getPrimerApellidoHermano(), dto.getSegundoApellidoHermano(),
                    null, null);
            sol.setHermano(hermano);
        } else {
            sol.setHermano(null);
        }
        sol = solicitudRepository.save(sol);
        f.setSolicitud(sol);

        f.setMismaInstitucionHermano(dto.isTieneHermano() && dto.isMismaInstitucionHermano());
        if (dto.isTieneHermano() && dto.getIdInstitucionHermano() != null) {
            f.setInstitucionHermano(buscarIde(dto.getIdInstitucionHermano()));
        } else {
            f.setInstitucionHermano(null);
        }

        solicitudColegioRepository.deleteBySolicitud_Id(sol.getId());
        int orden = 1;
        for (Long idIde : dto.getIdsColegios()) {
            IdeEntity ide = buscarIde(idIde);
            SolicitudColegioEntity sc = new SolicitudColegioEntity();
            sc.setSolicitud(sol);
            sc.setColegio(ide);
            sc.setOrdenPreferencia(orden++);
            sc.setFechaCrea(new Date());
            solicitudColegioRepository.save(sc);
        }

        f.setUltimaEtapa(2);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== ETAPA 3 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa3(Long id, BAEtapa3Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        PersonaEntity acudiente = personaHelper.upsert(f.getAcudiente(),
                dto.getTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                dto.getCelular(), dto.getCorreo());

        RefListado parentesco = null;
        if (dto.getParentesco() != null) {
            parentesco = lookupRef(dto.getParentesco(), "PARENTESCOS");
            acudiente.setIdParentesco(BigInteger.valueOf(parentesco.getIdRefListado()));
        }
        acudiente.setParentescoOtro(parentesco != null && "OTRO".equals(parentesco.getCodigo())
                ? nullIfEmpty(dto.getParentescoOtro()) : null);

        if (dto.getNivelEscolaridad() != null) {
            RefListado niv = lookupRef(dto.getNivelEscolaridad(), "NIVELES_ESCOLARIDAD");
            acudiente.setIdNvlEscolaridad(BigInteger.valueOf(niv.getIdRefListado()));
        }
        if (dto.getOcupacion() != null) {
            RefListado ocu = lookupRef(dto.getOcupacion(), "OCUPACIONES");
            acudiente.setIdOcupacion(BigInteger.valueOf(ocu.getIdRefListado()));
        }

        UbicacionEntity residencia = ubicacionHelper.upsert(acudiente.getUbicacion(),
                dto.getLocalidad(), dto.getBarrio(), dto.getBarrioOtro());
        residencia = ubicacionHelper.setDireccion(residencia, dto.getTipoVia(),
                dto.getDireccion(), dto.getDireccionComplemento(), null);
        acudiente.setUbicacion(residencia);
        acudiente = personaRepository.save(acudiente);

        f.setAcudiente(acudiente);
        f.setUltimaEtapa(3);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== ETAPA 4 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa4(Long id, BAEtapa4Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        List<BAEtapa4Dto.NoEstudiandoItem> items = dto.isExistenNoEstudiando()
                ? dto.getRangos() : java.util.Collections.emptyList();

        Map<String, BANoEstudiandoEntity> existentes = new HashMap<>();
        for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
            existentes.put(r.getRangoEdadCodigo(), r);
        }

        for (BAEtapa4Dto.NoEstudiandoItem item : items) {
            BANoEstudiandoEntity row = existentes.get(item.getRangoEdadCodigo());
            if (row == null) {
                row = new BANoEstudiandoEntity();
                row.setRangoEdadCodigo(item.getRangoEdadCodigo());
                f.agregarNoEstudiando(row);
            }
            row.setCuantos(item.getCuantos() != null ? item.getCuantos() : 0);
            if (item.getRazon() != null) {
                RefListado razon = lookupRef(item.getRazon(), "RAZONES_NOESCOLAR_BA");
                row.setRazon(razon);
                if ("OTROS_CUALES".equals(razon.getCodigo()) && item.getRazonOtra() != null) {
                    row.setRazonOtra(lookupRef(item.getRazonOtra(), "RAZONES_NOESCOLAR_OTRAS_BA"));
                } else {
                    row.setRazonOtra(null);
                }
            } else {
                row.setRazon(null);
                row.setRazonOtra(null);
            }
            existentes.remove(item.getRangoEdadCodigo());
        }

        Iterator<BANoEstudiandoEntity> it = f.getNoEstudiandoRangos().iterator();
        while (it.hasNext()) {
            if (existentes.containsKey(it.next().getRangoEdadCodigo())) {
                it.remove();
            }
        }

        f.setFinalizado(true);
        f.setUltimaEtapa(4);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== HELPERS ====================

    private RefListado lookupRef(RefListadoKVDto kv, String descripcion) {
        return resolver.resolve(kv, descripcion);
    }

    private IdeEntity buscarIde(Long idIde) {
        return ideRepository.findById(idIde)
                .orElseThrow(() -> new RecursoNoEncontradoException("Institución no encontrada: " + idIde));
    }

    private RefListado resolverRangoEdad(int edad) {
        String codigo;
        if (edad <= 5) codigo = "0_Y_5_ANOS";
        else if (edad <= 12) codigo = "6_Y_12_ANOS";
        else if (edad <= 18) codigo = "13_Y_18_ANOS";
        else if (edad <= 28) codigo = "19_Y_28_ANOS";
        else if (edad <= 59) codigo = "28_Y_59_ANOS";
        else codigo = "60_ANOS_EN_ADELANTE";
        return refRepo.findByCodigoAndDescripcionAndActivo(codigo, "RANGOS_EDADES", 1)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rango edad no encontrado: " + codigo));
    }

    private String nullIfEmpty(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
