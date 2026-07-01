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
import co.gov.educacionbogota.sicobertura.entities.EstudianteEntity;
import co.gov.educacionbogota.sicobertura.entities.IdeEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudColegioEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.enumerados.EConfiguracion;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.ConfiguracionRepository;
import co.gov.educacionbogota.sicobertura.repository.EstudianteRepository;
import co.gov.educacionbogota.sicobertura.repository.GradoRepository;
import co.gov.educacionbogota.sicobertura.repository.IdeRepository;
import co.gov.educacionbogota.sicobertura.repository.PersonaRepository;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudColegioRepository;
import co.gov.educacionbogota.sicobertura.repository.SolicitudRepository;

@Service
public class BAEtapaService {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_COLEGIOS_DEFAULT = 10;

    @Autowired private BAFormularioService formularioService;
    @Autowired private BAPersonaHelperService personaHelper;
    @Autowired private BAUbicacionHelperService ubicacionHelper;
    @Autowired private BARefResolverService resolver;
    @Autowired private RefListadoRepository refRepo;
    @Autowired private GradoRepository gradoRepository;
    @Autowired private PersonaRepository personaRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private SolicitudColegioRepository solicitudColegioRepository;
    @Autowired private IdeRepository ideRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;

    private int getMaxColegios() {
        return configuracionRepository
                .findByNombreConfiguracion(EConfiguracion.BA_MAX_COLEGIOS.name())
                .map(cfg -> {
                    try {
                        return Integer.parseInt(cfg.getValor().trim());
                    } catch (NumberFormatException ex) {
                        return MAX_COLEGIOS_DEFAULT;
                    }
                })
                .orElse(MAX_COLEGIOS_DEFAULT);
    }

    // ==================== ETAPA 1 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa1(Long id, BAEtapa1Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        EstudianteEntity estudiante = f.getEstudiante();
        PersonaEntity persona = (estudiante != null) ? estudiante.getPersona() : null;

        String celular = dto.isMayorEdadNombrePropio() ? dto.getCelular() : null;
        String correo  = dto.isMayorEdadNombrePropio() ? dto.getCorreo()  : null;
        persona = personaHelper.upsert(persona,
                dto.getCodigoTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                celular, correo);

        persona.setPaisNacimiento(resolver.resolveRequired(dto.getCodigoPaisNacimiento(), "PAIS"));
        String fechaIso = normalizarFechaIso(dto.getFechaNacimiento());
        try {
            persona.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(fechaIso));
        } catch (java.text.ParseException ex) {
            throw new ReglaNegocioException("Fecha nacimiento inválida: " + dto.getFechaNacimiento());
        }
        persona.setFechaNacimientoStr(fechaIso);
        persona.setSexo(resolver.resolveRequired(dto.getCodigoSexo(), "SEXOS"));

        RefListado etnia = resolver.resolveRequired(dto.getCodigoEtnia(), "ETNIAS");
        persona.setEtnia(etnia);
        persona.setEtniaOtro("OTRO".equals(etnia.getCodigo()) || "OTRA".equals(etnia.getCodigo())
                ? nullIfEmpty(dto.getEtniaOtro()) : null);

        persona.setDiscapacidad(dto.isDiscapacidad());
        if (dto.isDiscapacidad()) {
            persona.setTipoDiscapacidad(
                    resolver.resolveRequired(dto.getCodigoTipoDiscapacidad(), "DISCAPACIDADES"));
            persona.setCertDiscapacidad(Boolean.TRUE.equals(dto.getCertDiscapacidad()));
            persona.setSoporteDiscapacidad(nullIfEmpty(dto.getSoporteDiscapacidad()));
        } else {
            persona.setTipoDiscapacidad(null);
            persona.setCertDiscapacidad(false);
            persona.setSoporteDiscapacidad(null);
        }

        RefListado poblacion = resolver.resolveOptional(dto.getCodigoPoblacionDiferencial(), "POBLACION_EVENT_BA");
        if (poblacion != null) {
            persona.setPoblacionDiferencial(poblacion);
            persona.setPoblacionOtro("OTRO".equals(poblacion.getCodigo()) || "OTRA".equals(poblacion.getCodigo())
                    ? nullIfEmpty(dto.getPoblacionOtro()) : null);
        }
        persona.setGestante(Boolean.TRUE.equals(dto.getGestante()));

        UbicacionEntity residencia = ubicacionHelper.upsert(persona.getUbicacion(),
                dto.getCodigoLocalidad(), dto.getCodigoBarrio(), dto.getBarrioOtro());
        residencia = ubicacionHelper.setDireccion(residencia, dto.getCodigoTipoVia(),
                dto.getNumeroVia(), dto.getLetraVia(), dto.getSufijoVia(),
                dto.getNumeroSecVia(), dto.getNumeroFinVia(),
                dto.getDireccion(), dto.getDireccionComplemento(), null);
        persona.setUbicacion(residencia);
        persona = personaRepository.save(persona);

        if (estudiante == null) estudiante = new EstudianteEntity();
        estudiante.setPersona(persona);

        LocalDate fechaNac = LocalDate.parse(fechaIso, DT_FORMATTER);
        Period periodo = Period.between(fechaNac, LocalDate.now());
        estudiante.setEdadInt(periodo.getYears());
        estudiante.setEdadTxt(periodo.getYears() + " años, " + periodo.getMonths()
                + " meses, " + periodo.getDays() + " días.");
        estudiante.setRangoEdad(resolverRangoEdad(periodo.getYears()));
        estudiante = estudianteRepository.save(estudiante);

        f.setEstudiante(estudiante);
        f.setEtapa1Diligenciada(true);
        marcarUltimaEtapa(f, 1);
        actualizarFinalizado(f);
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
        int maxColegios = getMaxColegios();
        if (dto.getIdsColegios().size() > maxColegios) {
            throw new ReglaNegocioException("Máximo " + maxColegios + " instituciones en orden de preferencia");
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
        sol.setUltimoAnioAprobado(gradoRepository.findById(dto.getCodigoUltimoAnioAprobado())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Grado último año aprobado no encontrado: " + dto.getCodigoUltimoAnioAprobado())));
        sol.setGradoSolicitaCupo(gradoRepository.findById(dto.getCodigoGradoSolicitaCupo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Grado solicita cupo no encontrado: " + dto.getCodigoGradoSolicitaCupo())));

        sol.setTieneHermano(dto.isTieneHermano());
        if (dto.isTieneHermano()) {
            PersonaEntity hermano = personaHelper.upsert(sol.getHermano(),
                    dto.getCodigoTipoDocumentoHermano(), dto.getNumeroDocumentoHermano(),
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

        f.setEtapa2Diligenciada(true);
        marcarUltimaEtapa(f, 2);
        actualizarFinalizado(f);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== ETAPA 3 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa3(Long id, BAEtapa3Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        PersonaEntity acudiente = personaHelper.upsert(f.getAcudiente(),
                dto.getCodigoTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                dto.getCelular(), dto.getCorreo());

        RefListado parentesco = resolver.resolveOptional(dto.getCodigoParentesco(), "PARENTESCOS");
        if (parentesco != null) {
            acudiente.setIdParentesco(BigInteger.valueOf(parentesco.getIdRefListado()));
        }
        acudiente.setParentescoOtro(parentesco != null && "OTRO".equals(parentesco.getCodigo())
                ? nullIfEmpty(dto.getParentescoOtro()) : null);

        RefListado niv = resolver.resolveOptional(dto.getCodigoNivelEscolaridad(), "NIVELES_ESCOLARIDAD");
        if (niv != null) {
            acudiente.setIdNvlEscolaridad(BigInteger.valueOf(niv.getIdRefListado()));
        }
        RefListado ocu = resolver.resolveOptional(dto.getCodigoOcupacion(), "OCUPACIONES");
        if (ocu != null) {
            acudiente.setIdOcupacion(BigInteger.valueOf(ocu.getIdRefListado()));
        }

        UbicacionEntity residencia = ubicacionHelper.upsert(acudiente.getUbicacion(),
                dto.getCodigoLocalidad(), dto.getCodigoBarrio(), dto.getBarrioOtro());
        residencia = ubicacionHelper.setDireccion(residencia, dto.getCodigoTipoVia(),
                dto.getNumeroVia(), dto.getLetraVia(), dto.getSufijoVia(),
                dto.getNumeroSecVia(), dto.getNumeroFinVia(),
                dto.getDireccion(), dto.getDireccionComplemento(), null);
        acudiente.setUbicacion(residencia);
        acudiente = personaRepository.save(acudiente);

        f.setAcudiente(acudiente);
        f.setEtapa3Diligenciada(true);
        marcarUltimaEtapa(f, 3);
        actualizarFinalizado(f);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== ETAPA 4 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarEtapa4(Long id, BAEtapa4Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        List<BAEtapa4Dto.NoEstudiandoItem> items = dto.isExistenNoEstudiando()
                ? dto.getRangos() : java.util.Collections.emptyList();

        Map<Long, BANoEstudiandoEntity> existentes = new HashMap<>();
        for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
            if (r.getRangoEdadCodigo() != null) {
                try {
                    existentes.put(Long.parseLong(r.getRangoEdadCodigo()), r);
                } catch (NumberFormatException ignored) {
                    // datos legacy con codigo string — se ignoran
                }
            }
        }

        for (BAEtapa4Dto.NoEstudiandoItem item : items) {
            RefListado rango = resolver.resolveRequired(item.getCodigoRangoEdad(), "RANGOS_EDADES_BA");
            BANoEstudiandoEntity row = existentes.get(rango.getIdRefListado());
            if (row == null) {
                row = new BANoEstudiandoEntity();
                row.setRangoEdadCodigo(String.valueOf(rango.getIdRefListado()));
                f.agregarNoEstudiando(row);
            }
            row.setCuantos(item.getCuantos() != null ? item.getCuantos() : 0);
            RefListado razon = resolver.resolveOptional(item.getCodigoRazon(), "RAZONES_NOESCOLAR_BA");
            if (razon != null) {
                row.setRazon(razon);
                if ("OTROS_CUALES".equals(razon.getCodigo()) && item.getCodigoRazonOtra() != null) {
                    row.setRazonOtra(resolver.resolveRequired(item.getCodigoRazonOtra(), "RAZONES_NOESCOLAR_OTRAS_BA"));
                } else {
                    row.setRazonOtra(null);
                }
            } else {
                row.setRazon(null);
                row.setRazonOtra(null);
            }
            existentes.remove(rango.getIdRefListado());
        }

        Iterator<BANoEstudiandoEntity> it = f.getNoEstudiandoRangos().iterator();
        while (it.hasNext()) {
            BANoEstudiandoEntity r = it.next();
            if (r.getRangoEdadCodigo() != null) {
                try {
                    Long codigoLong = Long.parseLong(r.getRangoEdadCodigo());
                    if (existentes.containsKey(codigoLong)) {
                        it.remove();
                    }
                } catch (NumberFormatException ignored) {
                    // ignorar legacy
                }
            }
        }

        f.setEtapa4Diligenciada(true);
        marcarUltimaEtapa(f, 4);
        actualizarFinalizado(f);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== HELPERS ====================

    private void marcarUltimaEtapa(BusquedaActivaFormularioEntity f, int etapa) {
        if (etapa > f.getUltimaEtapa()) f.setUltimaEtapa(etapa);
    }

    private void actualizarFinalizado(BusquedaActivaFormularioEntity f) {
        boolean todas = f.isEtapa1Diligenciada() && f.isEtapa2Diligenciada()
                && f.isEtapa3Diligenciada() && f.isEtapa4Diligenciada();
        f.setFinalizado(todas);
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

    /** Acepta yyyy-MM-dd o ISO completo con timezone (ej. 2026-06-10T05:00:00.000Z) y normaliza a yyyy-MM-dd. */
    private String normalizarFechaIso(String fecha) {
        if (fecha == null || fecha.isEmpty()) return fecha;
        int t = fecha.indexOf('T');
        return t > 0 ? fecha.substring(0, t) : fecha;
    }

    private String nullIfEmpty(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
