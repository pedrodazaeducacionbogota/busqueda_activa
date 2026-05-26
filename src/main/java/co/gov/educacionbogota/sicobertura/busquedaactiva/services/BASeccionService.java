package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.BANoEstudiandoRepository;
import co.gov.educacionbogota.sicobertura.entities.EstudianteEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SedeEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.exception.RecursoNoEncontradoException;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.EstudianteRepository;
import co.gov.educacionbogota.sicobertura.repository.PersonaRepository;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import co.gov.educacionbogota.sicobertura.repository.SedeRepository;

/**
 * Actualiza secciones 1-7 del formulario BA. Cada método incrementa `ultimaSeccion`.
 * Sección 7 marca `finalizado=true`. Lookups RefListado por (codigo, descripcion, activo=1).
 */
@Service
public class BASeccionService {

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired private BAFormularioService formularioService;
    @Autowired private BAPersonaHelperService personaHelper;
    @Autowired private BAUbicacionHelperService ubicacionHelper;
    @Autowired private RefListadoRepository refRepo;
    @Autowired private SedeRepository sedeRepository;
    @Autowired private PersonaRepository personaRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @SuppressWarnings("unused")
    @Autowired private BANoEstudiandoRepository noEstudiandoRepository;

    // ==================== SECCIÓN 1 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion1(Long id, BASeccion1Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        RefListado actividad = lookupRef(dto.getCodigoActividad(), "ACTIVIDADES_BA");
        f.setActividad(actividad);
        f.setActividadOtra(nullIfEmpty(dto.getActividadOtra()));
        f.setNombreEventoFeria(nullIfEmpty(dto.getNombreEventoFeria()));
        if (dto.getPoblacionEvento() != null && !dto.getPoblacionEvento().isEmpty()) {
            f.setPoblacionEvento(String.join(",", dto.getPoblacionEvento()));
        } else {
            f.setPoblacionEvento(null);
        }

        UbicacionEntity ubicacion = ubicacionHelper.upsert(f.getUbicacion(),
                dto.getCodigoLocalidad(), dto.getCodigoBarrio(), dto.getBarrioOtro());
        f.setUbicacion(ubicacion);

        f.setUltimaSeccion(1);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 2 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion2(Long id, BASeccion2Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        PersonaEntity persona = personaHelper.upsert(f.getAtiendeVisita(),
                dto.getCodigoTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                dto.getCelulares(), dto.getEmails());

        // Ubicación de residencia (separada de la visita)
        UbicacionEntity residencia = ubicacionHelper.upsert(persona.getUbicacion(),
                dto.getCodigoLocalidad(), dto.getCodigoBarrio(), dto.getBarrioOtro());
        residencia = ubicacionHelper.setDireccion(residencia, dto.getDireccion(),
                dto.getDireccionComplemento(), dto.getEstrato());
        persona.setUbicacion(residencia);

        if (dto.getCodigoCategoriaSisben() != null && !dto.getCodigoCategoriaSisben().isEmpty()) {
            RefListado sisben = lookupRef(dto.getCodigoCategoriaSisben(), "CATEGORIAS_SISBEN");
            persona.setIdCatSisben(java.math.BigInteger.valueOf(sisben.getIdRefListado()));
        }
        persona = personaRepository.save(persona);

        f.setAtiendeVisita(persona);
        f.setUltimaSeccion(2);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 3 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion3(Long id, BASeccion3Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        f.setColegiosCerca(dto.isColegiosCerca());
        if (dto.getIdeColegioCerca() != null) {
            SedeEntity sede = sedeRepository.findById(dto.getIdeColegioCerca())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Sede colegio no encontrada: " + dto.getIdeColegioCerca()));
            f.setIdeColegioCerca(sede);
        }
        f.setColegioCercaCual(nullIfEmpty(dto.getColegioCercaCual()));

        f.setJardinesCerca(dto.isJardinesCerca());
        if (dto.getIdeJardinCerca() != null) {
            SedeEntity sede = sedeRepository.findById(dto.getIdeJardinCerca())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Sede jardín no encontrada: " + dto.getIdeJardinCerca()));
            f.setIdeJardinCerca(sede);
        }
        f.setJardinCercaCual(nullIfEmpty(dto.getJardinCercaCual()));

        f.setUltimaSeccion(3);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 4 (normalizada) ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion4(Long id, BASeccion4Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        // Map existentes por rangoEdadCodigo para upsert
        Map<String, BANoEstudiandoEntity> existentes = new HashMap<>();
        for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
            existentes.put(r.getRangoEdadCodigo(), r);
        }

        for (BASeccion4Dto.NoEstudiandoItem item : dto.getRangos()) {
            BANoEstudiandoEntity row = existentes.get(item.getRangoEdadCodigo());
            if (row == null) {
                row = new BANoEstudiandoEntity();
                row.setRangoEdadCodigo(item.getRangoEdadCodigo());
                f.agregarNoEstudiando(row);
            }
            row.setCuantos(item.getCuantos() != null ? item.getCuantos() : 0);
            if (item.getCuantos() != null && item.getCuantos() > 0 && item.getCodigoRazon() != null) {
                row.setRazon(lookupRef(item.getCodigoRazon(), "RAZONES_NOESCOLAR_BA"));
            } else {
                row.setRazon(null);
            }
            row.setRazonOtra(nullIfEmpty(item.getRazonOtra()));
            existentes.remove(item.getRangoEdadCodigo());
        }

        // Borrar rangos huérfanos (no enviados en el DTO)
        Iterator<BANoEstudiandoEntity> it = f.getNoEstudiandoRangos().iterator();
        while (it.hasNext()) {
            BANoEstudiandoEntity row = it.next();
            if (existentes.containsKey(row.getRangoEdadCodigo())) {
                it.remove();
            }
        }

        f.setUltimaSeccion(4);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 5 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion5(Long id, BASeccion5Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        PersonaEntity acudiente;
        if (dto.isAtiendeVisitaAcudiente()) {
            // Reusa persona de sección 2
            if (f.getAtiendeVisita() == null) {
                throw new ReglaNegocioException("No se puede marcar atiendeVisitaAcudiente sin sección 2");
            }
            acudiente = f.getAtiendeVisita();
        } else {
            acudiente = personaHelper.upsert(f.getAcudiente(),
                    dto.getCodigoTipoDocumento(), dto.getNumeroDocumento(),
                    dto.getPrimerNombre(), dto.getSegundoNombre(),
                    dto.getPrimerApellido(), dto.getSegundoApellido(),
                    dto.getCelulares(), dto.getEmails());
        }

        // Datos familiares en PersonaEntity (BigInteger refs)
        if (dto.getCodigoParentesco() != null) {
            RefListado parentesco = lookupRef(dto.getCodigoParentesco(), "PARENTESCOS");
            acudiente.setIdParentesco(java.math.BigInteger.valueOf(parentesco.getIdRefListado()));
        }
        acudiente.setParentescoOtro(nullIfEmpty(dto.getFamiliarOtro()));
        if (dto.getCodigoNivelEscolaridad() != null) {
            RefListado niv = lookupRef(dto.getCodigoNivelEscolaridad(), "NIVELES_ESCOLARIDAD");
            acudiente.setIdNvlEscolaridad(java.math.BigInteger.valueOf(niv.getIdRefListado()));
        }
        if (dto.getCodigoOcupacion() != null) {
            RefListado ocu = lookupRef(dto.getCodigoOcupacion(), "OCUPACIONES");
            acudiente.setIdOcupacion(java.math.BigInteger.valueOf(ocu.getIdRefListado()));
        }
        acudiente = personaRepository.save(acudiente);

        f.setAcudiente(acudiente);
        f.setAtiendeVisitaAcudiente(dto.isAtiendeVisitaAcudiente());
        f.setUltimaSeccion(5);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 6 ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion6(Long id, BASeccion6Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        EstudianteEntity estudiante = f.getEstudiante();
        PersonaEntity persona = (estudiante != null) ? estudiante.getPersona() : null;

        persona = personaHelper.upsert(persona,
                dto.getCodigoTipoDocumento(), dto.getNumeroDocumento(),
                dto.getPrimerNombre(), dto.getSegundoNombre(),
                dto.getPrimerApellido(), dto.getSegundoApellido(),
                null, null);

        // País nacimiento + municipio exp doc
        RefListado pais = lookupRef(dto.getPaisNacimiento(), "PAISES");
        persona.setPaisNacimiento(pais);
        if ("COL".equals(dto.getPaisNacimiento()) && dto.getMunicipioExpDoc() != null) {
            RefListado mun = lookupRef(dto.getMunicipioExpDoc(), "MUNICIPIOS");
            persona.setIdMunicipioExpDoc(java.math.BigInteger.valueOf(mun.getIdRefListado()));
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            persona.setFechaNacimiento(sdf.parse(dto.getFechaNacimiento()));
        } catch (java.text.ParseException ex) {
            throw new ReglaNegocioException("Fecha nacimiento inválida: " + dto.getFechaNacimiento());
        }
        persona.setFechaNacimientoStr(dto.getFechaNacimiento());

        persona.setSexo(lookupRef(dto.getCodigoSexo(), "SEXOS"));
        if (dto.getCodigoGenero() != null) {
            RefListado g = lookupRef(dto.getCodigoGenero(), "GENEROS");
            persona.setIdGenero(java.math.BigInteger.valueOf(g.getIdRefListado()));
        }
        persona.setDiscapacidad(dto.isDiscapacidad());
        if (dto.isDiscapacidad() && dto.getCodigoTipoDiscapacidad() != null) {
            persona.setTipoDiscapacidad(lookupRef(dto.getCodigoTipoDiscapacidad(), "TIPOS_DISCAPACIDAD"));
            persona.setCertDiscapacidad(dto.isCertDiscapacidad());
        }
        persona = personaRepository.save(persona);

        if (estudiante == null) {
            estudiante = new EstudianteEntity();
        }
        estudiante.setPersona(persona);
        if (dto.getCodigoEnfoqueDiferencial() != null) {
            estudiante.setEnfoqueDiferencial(lookupRef(dto.getCodigoEnfoqueDiferencial(), "ENFOQUES_DIFERENCIAL"));
        }

        // Edad calculada snapshot
        LocalDate fechaNac = LocalDate.parse(dto.getFechaNacimiento(), DT_FORMATTER);
        Period periodo = Period.between(fechaNac, LocalDate.now());
        estudiante.setEdadInt(periodo.getYears());
        estudiante.setEdadTxt(periodo.getYears() + " años, " + periodo.getMonths()
                + " meses, " + periodo.getDays() + " días.");
        estudiante.setRangoEdad(resolverRangoEdad(periodo.getYears()));

        estudiante = estudianteRepository.save(estudiante);
        f.setEstudiante(estudiante);
        f.setUltimaSeccion(6);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== SECCIÓN 7 (finaliza) ====================
    @Transactional
    public ResponseBASeccionesDto actualizarSeccion7(Long id, BASeccion7Dto dto) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);

        f.setUltimoAnioEstudio(lookupRef(dto.getCodigoUltimoAnioEstudio(), "GRADOS_ESCOLARES"));
        f.setRepitioUltimoAnio(dto.isRepitioUltimoAnio());
        if (dto.isRepitioUltimoAnio() && dto.getCodigoVecesRepitio() != null) {
            f.setVecesRepitioAnio(lookupRef(dto.getCodigoVecesRepitio(), "VECES_REPITIO"));
        }
        f.setUltimoAnioAprobado(lookupRef(dto.getCodigoUltimoAnioAprobado(), "GRADOS_ESCOLARES"));

        SedeEntity sedeSolicita = sedeRepository.findById(dto.getIdeSolicitaCupo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Sede solicita cupo no encontrada: " + dto.getIdeSolicitaCupo()));
        f.setIdeSolicitaCupo(sedeSolicita);
        f.setGradoSolicitaCupo(lookupRef(dto.getCodigoGradoSolicitaCupo(), "GRADOS_ESCOLARES"));

        f.setFinalizado(true);
        f.setUltimaSeccion(7);
        formularioService.guardar(f);
        return new ResponseBASeccionesDto(id, f.getProfesional().getId());
    }

    // ==================== HELPERS ====================

    private RefListado lookupRef(String codigo, String descripcion) {
        return refRepo.findByCodigoAndDescripcionAndActivo(codigo, descripcion, 1)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ref no encontrada: codigo=" + codigo + " descripcion=" + descripcion));
    }

    private RefListado resolverRangoEdad(int edad) {
        String codigo;
        if (edad <= 5) codigo = "0-5";
        else if (edad <= 12) codigo = "6-12";
        else if (edad <= 18) codigo = "13-18";
        else if (edad <= 28) codigo = "19-28";
        else if (edad <= 59) codigo = "29-59";
        else codigo = "60+";
        return lookupRef(codigo, "RANGOS_EDADES");
    }

    private String nullIfEmpty(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
