package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.QuestionResponse;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.CaracterizacionEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.FactorDesescolarizacion;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.CaracterizacionRepository;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.RegistroBusquedaActivaRepository;
import co.gov.educacionbogota.sicobertura.entities.IdeEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.entities.UbicacionEntity;
import co.gov.educacionbogota.sicobertura.repository.IdeRepository;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class QuestionFlowServiceImpl implements QuestionFlowService {

    @Autowired
    private RegistroBusquedaActivaRepository repository;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private RefListadoRepository refListadoRepository;

    @Autowired
    private IdeRepository ideRepository;

    @Autowired
    private CaracterizacionRepository caracterizacionRepo;

    // Codigos de listados padre en ref_listado (alineados al seed)
    private static final String COD_TIPOS_DOCUMENTO = "TIPOS_DOCUMENTO";
    private static final String COD_SEXO = "SEXO";
    private static final String COD_ETNIA = "ETNIA";
    private static final String COD_DISCAPACIDAD = "DISCAPACIDAD";
    private static final String COD_GRADO_APROBADO = "GRADO_APROBADO";
    private static final String COD_GRADO_SOLICITADO = "GRADO_SOLICITADO";
    private static final String COD_PARENTESCOS = "PARENTESCOS";
    private static final String COD_NIVELES_ESCOLARIDAD = "NIVELES_ESCOLARIDAD";
    private static final String COD_OCUPACIONES = "OCUPACIONES";
    private static final String COD_LOCALIDADES_BOGOTA = "LOCALIDADES_BOGOTA";

    private static final Map<Integer, QuestionDef> QUESTIONS = new LinkedHashMap<>();
    private static final Map<String, Integer> KEY_TO_ID = new HashMap<>();

    static {
        // --- ETAPA 1: ESTUDIANTE ---
        addQuestion(1, "estudiante_primerNombre", "Primer Nombre del Estudiante", null);
        addQuestion(2, "estudiante_segundoNombre", "Segundo Nombre del Estudiante", null);
        addQuestion(3, "estudiante_primerApellido", "Primer Apellido del Estudiante", null);
        addQuestion(4, "estudiante_segundoApellido", "Segundo Apellido del Estudiante", null);
        addQuestion(5, "estudiante_tipoDocumento", "Tipo de Documento", Arrays.asList("RC", "TI", "CC", "CE", "NES", "PEP", "PPT", "VI"));
        addQuestion(6, "estudiante_numeroDocumento", "Numero de Documento", null);
        addQuestion(7, "estudiante_fechaNacimiento", "Fecha de Nacimiento (yyyy-MM-dd)", null);
        addQuestion(8, "estudiante_sexo", "Sexo", Arrays.asList("Femenino", "Masculino"));
        addQuestion(9, "estudiante_gestante", "La estudiante es gestante?", Arrays.asList("Si", "No"));
        addQuestion(10, "estudiante_etnia", "Etnia", Arrays.asList("Indigena", "Afrodescendiente", "Raizal", "Roms", "Ninguna"));
        addQuestion(11, "estudiante_tipoDiscapacidad", "Tipo de Discapacidad", Arrays.asList("Fisica", "Auditiva", "Visual", "Sordoceguera",
                "Intelectual", "Psicosocial", "Multiple", "Ninguna"));
        addQuestion(12, "estudiante_poblacionDiferencial", "Se reconoce como parte de alguna poblacion (victima, desplazado, etc)?",
                Arrays.asList("Si", "No"));
        addQuestion(13, "estudiante_localidadResidencia", "Localidad de Residencia",
                Arrays.asList("Usaquen", "Chapinero", "Santa Fe", "San Cristobal", "Usme", "Tunjuelito", "Bosa",
                        "Kennedy", "Fontibon", "Engativa", "Suba", "Barrios Unidos", "Teusaquillo", "Los Martires",
                        "Antonio Narino", "Puente Aranda", "La Candelaria", "Rafael Uribe Uribe", "Ciudad Bolivar",
                        "Sumapaz"));
        addQuestion(14, "estudiante_barrio", "Barrio", null);
        addQuestion(15, "estudiante_direccion", "Direccion de Vivienda", null);

        // --- ETAPA 2: CUPO ---
        addQuestion(16, "solicitud_ultimoAnioAprobado", "Ultimo Anio Aprobado",
                Arrays.asList("Preescolar", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        addQuestion(17, "solicitud_localidadInteres", "Localidad de la Institucion de interes", null);
        addQuestion(18, "solicitud_institucionesInteres", "Nombre de las Instituciones", null);
        addQuestion(19, "solicitud_gradoSolicita", "Grado Solicitado", null);
        addQuestion(20, "solicitud_tieneHermano", "El Estudiante tiene hermanos en una institucion oficial?", Arrays.asList("Si", "No"));

        // Ramificacion Azul (Hermanos)
        addQuestion(21, "hermano_tipoDocumento", "Tipo de Documento del Hermano",
                Arrays.asList("RC", "TI", "CC", "CE", "NES", "PEP", "PPT", "VI"));
        addQuestion(22, "hermano_numeroDocumento", "Numero de Documento del Hermano", null);
        addQuestion(23, "hermano_nombres", "Nombres y Apellidos del Hermano", null);
        addQuestion(24, "solicitud_mismaInstitucion", "Requiere que el estudiante este en la misma institucion?", Arrays.asList("Si", "No"));
        addQuestion(25, "hermano_institucion", "Nombre de la Institucion del Hermano", null);

        // --- ETAPA 3: RESPONSABLE ---
        addQuestion(26, "responsable_tipoDocumento", "Tipo de Documento del Responsable", Arrays.asList("CC", "CE", "PEP", "PPT", "VI"));
        addQuestion(27, "responsable_numeroDocumento", "Numero de Documento del Responsable", null);
        addQuestion(28, "responsable_nombres", "Nombres y Apellidos del Responsable", null);
        addQuestion(29, "responsable_email", "Correo Electronico", null);
        addQuestion(30, "responsable_celular", "Numero de Contacto", null);
        addQuestion(31, "responsable_parentesco", "Parentesco", Arrays.asList("Padre", "Madre", "Abuela - Abuelo", "Tio - Tia", "Hermano - Hermana"));
        addQuestion(32, "responsable_escolaridad", "Nivel de Escolaridad", Arrays.asList("Preescolar", "Basica primaria (1 - 5)", "Basica secundaria (6 - 9)",
                "Media (10 - 13)", "Tecnico", "Tecnologico", "Universitaria completa (con titulo)", "Ninguno"));
        addQuestion(33, "responsable_ocupacion", "Ocupacion", null);
        addQuestion(34, "responsable_localidad", "Localidad Responsable", null);
        addQuestion(35, "responsable_barrio", "Barrio Responsable", null);
        addQuestion(36, "responsable_direccion", "Direccion Responsable", null);

        // --- ETAPA 4: FACTORES DESESCOLARIZACION ---
        addQuestion(37, "factores_tieneNinosSinEstudio",
                "En su nucleo familiar existen NNAJ que no se encuentren estudiando?",
                Arrays.asList("Si", "No"));
        addQuestion(38, "factores_cantidadNinosSinEstudio", "Cuantos ninos conoce que no estan estudiando?", null);
        addQuestion(39, "factores_rangoEdad", "Rango de edad de las personas que no estan estudiando",
                Arrays.asList("3-5 anios", "6-11 anios", "12-15 anios", "16-17 anios", "18+ anios"));
        addQuestion(40, "factores_razonDesescolarizacion", "Cuales son las razones por las que no estan estudiando?", null);
    }

    private static void addQuestion(int id, String key, String text, List<String> options) {
        QUESTIONS.put(id, new QuestionDef(id, key, text, options));
        KEY_TO_ID.put(key, id);
    }

    @Override
    @Transactional
    public QuestionResponse processAnswer(Long registroId, Integer questionId, String answer) {
        RegistroBusquedaActiva registro = repository.findById(registroId)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        saveAnswerToEntity(registro, questionId, answer);

        Integer nextId = determineNextQuestion(registro, questionId, answer);
        registro.setPreguntaActualId(nextId);
        repository.save(registro);

        checkMilestones(registro, nextId);

        return buildResponse(registro, nextId);
    }

    @Override
    @Transactional
    public QuestionResponse processBatchAnswers(Long registroId, Map<String, String> answers) {
        RegistroBusquedaActiva registro = repository.findById(registroId)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        Integer lastQId = 1;
        String lastAns = "";

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            Integer qId = KEY_TO_ID.get(entry.getKey());
            if (qId != null) {
                saveAnswerToEntity(registro, qId, entry.getValue());
                lastQId = qId;
                lastAns = entry.getValue();
            }
        }

        Integer nextId = determineNextQuestion(registro, lastQId, lastAns);
        registro.setPreguntaActualId(nextId);
        repository.save(registro);

        checkMilestones(registro, nextId);

        return buildResponse(registro, nextId);
    }

    private void checkMilestones(RegistroBusquedaActiva registro, Integer nextId) {
        if (nextId == 26) {
            reporteService.generarReportePdf(registro);
        } else if (nextId == -1) {
            registro.setEstado("Finalizado");
            repository.save(registro);
            reporteService.generarReportePdf(registro);
        }
    }

    @Override
    public QuestionResponse.QuestionDTO getQuestion(Integer questionId) {
        if (questionId == null || questionId == -1)
            return null;
        QuestionDef def = QUESTIONS.get(questionId);
        if (def == null)
            return null;

        List<QuestionResponse.OptionDTO> options = null;
        if (def.options != null) {
            options = new ArrayList<>();
            for (String opt : def.options) {
                options.add(QuestionResponse.OptionDTO.builder().id(opt).response(opt).build());
            }
        }

        return QuestionResponse.QuestionDTO.builder()
                .id(def.id)
                .question(def.text)
                .type(def.options != null && !def.options.isEmpty() ? "OPCIONES" : "ABIERTA")
                .options(options)
                .build();
    }

    @Override
    public List<QuestionResponse.QuestionDTO> getAllQuestions() {
        List<QuestionResponse.QuestionDTO> all = new ArrayList<>();
        for (Integer id : QUESTIONS.keySet()) {
            all.add(getQuestion(id));
        }
        return all;
    }

    @Override
    public QuestionResponse getInitialState(Long registroId) {
        RegistroBusquedaActiva registro = repository.findById(registroId)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        Integer currentId = registro.getPreguntaActualId();
        if (currentId == null)
            currentId = 1;

        return buildResponse(registro, currentId);
    }

    private QuestionResponse buildResponse(RegistroBusquedaActiva registro, Integer nextId) {
        Map<String, Object> history = buildHistory(registro);
        QuestionResponse.QuestionDTO nextQ = getQuestion(nextId);
        return QuestionResponse.builder()
                .datos(history)
                .siguientePregunta(nextQ)
                .build();
    }

    private Integer determineNextQuestion(RegistroBusquedaActiva registro, Integer currentId, String answer) {
        if (currentId == 20) {
            if ("Si".equalsIgnoreCase(answer) || "Sí".equalsIgnoreCase(answer))
                return 21;
            return 26;
        }
        if (currentId == 25)
            return 26;

        if (currentId == 37) {
            if ("Si".equalsIgnoreCase(answer) || "Sí".equalsIgnoreCase(answer))
                return 38;
            return -1;
        }
        if (currentId == 40)
            return -1;

        int nextId = currentId + 1;
        return QUESTIONS.containsKey(nextId) ? nextId : -1;
    }

    private void saveAnswerToEntity(RegistroBusquedaActiva registro, Integer qId, String ans) {
        if (registro.getEstudiante() == null)
            registro.setEstudiante(new PersonaEntity());
        if (registro.getSolicitudCupo() == null)
            registro.setSolicitudCupo(new SolicitudEntity());
        if (registro.getResponsable() == null)
            registro.setResponsable(new PersonaEntity());

        PersonaEntity e = registro.getEstudiante();
        SolicitudEntity s = registro.getSolicitudCupo();
        PersonaEntity r = registro.getResponsable();

        switch (qId) {
            case 1: e.setPrimerNombre(ans); break;
            case 2: e.setSegundoNombre(ans); break;
            case 3: e.setPrimerApellido(ans); break;
            case 4: e.setSegundoApellido(ans); break;
            case 5: e.setTipoDocumento(findRefByValorTxtOrNombre(COD_TIPOS_DOCUMENTO, ans)); break;
            case 6: e.setNumeroDocumento(ans); break;
            case 7:
                try { e.setFechaNacimiento(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(ans)); } catch (Exception ex) { /* fecha invalida */ }
                break;
            case 8: e.setSexo(findRefByNombre(COD_SEXO, ans)); break;
            case 9: e.setGestante("Si".equalsIgnoreCase(ans) || "Sí".equalsIgnoreCase(ans)); break;
            case 10: e.setEtnia(findRefByNombre(COD_ETNIA, ans)); break;
            case 11: e.setTipoDiscapacidad(findRefByNombre(COD_DISCAPACIDAD, ans)); break;
            case 12: /* poblacionDiferencial: no hay campo directo. Omitido. */ break;
            case 13: {
                registro.setLocalidad(ans);
                RefListado localidad = findLocalidad(ans);
                if (localidad != null) getOrCreateCaracterizacion(s).setLocalidadInstitucion(localidad);
                break;
            }
            case 14: registro.setBarrio(ans); break;
            case 15:
                if (e.getUbicacion() == null) e.setUbicacion(new UbicacionEntity());
                e.getUbicacion().setDireccion(ans);
                break;

            case 16: s.setUltimoAnioAprobado(findRefByNombre(COD_GRADO_APROBADO, ans)); break;
            case 17: {
                RefListado localidad = findLocalidad(ans);
                if (localidad != null) getOrCreateCaracterizacion(s).setLocalidadInstitucion(localidad);
                break;
            }
            case 18: {
                IdeEntity ide = findInstitucion(ans);
                if (ide != null) getOrCreateCaracterizacion(s).setInstitucion(ide);
                break;
            }
            case 19: s.setGradoSolicitaCupo(findRefByNombre(COD_GRADO_SOLICITADO, ans)); break;
            case 20: s.setTieneHermano("Si".equalsIgnoreCase(ans) || "Sí".equalsIgnoreCase(ans)); break;
            case 21: /* tipoDocumento hermano: PersonaEntity hermano necesita carga separada */ break;
            case 22: s.setPerIdHermano(ans); break;
            case 23:
                if (s.getHermano() == null) s.setHermano(new PersonaEntity());
                s.getHermano().setPrimerNombre(ans);
                break;
            case 24: getOrCreateCaracterizacion(s).setMismaInstitucion("Si".equalsIgnoreCase(ans) || "Sí".equalsIgnoreCase(ans)); break;
            case 25: {
                IdeEntity ide = findInstitucion(ans);
                if (ide != null) getOrCreateCaracterizacion(s).setInstitucionHermano(ide);
                break;
            }

            case 26: r.setTipoDocumento(findRefByValorTxtOrNombre(COD_TIPOS_DOCUMENTO, ans)); break;
            case 27: r.setNumeroDocumento(ans); break;
            case 28: r.setPrimerNombre(ans); break;
            case 29: r.setEmails(ans); break;
            case 30: r.setCelulares(ans); break;
            case 31: {
                RefListado parentesco = findRefByNombre(COD_PARENTESCOS, ans);
                if (parentesco != null) r.setIdParentesco(BigInteger.valueOf(parentesco.getIdRefListado()));
                break;
            }
            case 32: {
                RefListado nivel = findRefByNombre(COD_NIVELES_ESCOLARIDAD, ans);
                if (nivel != null) r.setIdNvlEscolaridad(BigInteger.valueOf(nivel.getIdRefListado()));
                break;
            }
            case 33: {
                RefListado ocupacion = findRefByNombre(COD_OCUPACIONES, ans);
                if (ocupacion != null) r.setIdOcupacion(BigInteger.valueOf(ocupacion.getIdRefListado()));
                break;
            }

            case 37:
                registro.setTieneNinosSinEstudio("Si".equalsIgnoreCase(ans) || "Sí".equalsIgnoreCase(ans));
                break;
            case 38:
                try {
                    registro.setCantidadNinosSinEstudio(Integer.parseInt(ans));
                } catch (Exception ex) {
                    registro.setCantidadNinosSinEstudio(0);
                }
                break;
            case 39:
                if (registro.getFactoresDesescolarizacion() == null)
                    registro.setFactoresDesescolarizacion(new ArrayList<>());
                if (registro.getFactoresDesescolarizacion().isEmpty()) {
                    FactorDesescolarizacion f = new FactorDesescolarizacion();
                    f.setRegistro(registro);
                    registro.getFactoresDesescolarizacion().add(f);
                }
                registro.getFactoresDesescolarizacion().get(0).setRangoEdad(ans);
                break;
            case 40:
                if (registro.getFactoresDesescolarizacion() != null
                        && !registro.getFactoresDesescolarizacion().isEmpty()) {
                    registro.getFactoresDesescolarizacion().get(0).setRazonPrincipal(ans);
                }
                break;
            default: break;
        }
    }

    // ===== Helpers de lookup en RefListado =====

    private RefListado findRefByNombre(String codigoPadre, String nombre) {
        if (nombre == null) return null;
        return refListadoRepository.findHijosByCodigoPadre(codigoPadre).stream()
                .filter(rl -> rl.getNombre() != null && rl.getNombre().equalsIgnoreCase(nombre))
                .findFirst().orElse(null);
    }

    private RefListado findRefByValorTxtOrNombre(String codigoPadre, String valor) {
        if (valor == null) return null;
        return refListadoRepository.findHijosByCodigoPadre(codigoPadre).stream()
                .filter(rl -> valor.equalsIgnoreCase(rl.getValorTxt())
                        || valor.equalsIgnoreCase(rl.getNombre())
                        || valor.equalsIgnoreCase(rl.getCodigo()))
                .findFirst().orElse(null);
    }

    private RefListado findLocalidad(String nombre) {
        if (nombre == null) return null;
        String needle = nombre.toLowerCase();
        return refListadoRepository.findHijosByCodigoPadre(COD_LOCALIDADES_BOGOTA).stream()
                .filter(rl -> rl.getNombre() != null && rl.getNombre().toLowerCase().contains(needle))
                .findFirst().orElse(null);
    }

    private IdeEntity findInstitucion(String nombre) {
        if (nombre == null) return null;
        String needle = nombre.toLowerCase();
        return ideRepository.findByActivoTrueOrderByNombre().stream()
                .filter(i -> i.getNombre() != null && i.getNombre().toLowerCase().contains(needle))
                .findFirst().orElse(null);
    }

    private CaracterizacionEntity getOrCreateCaracterizacion(SolicitudEntity s) {
        return caracterizacionRepo.findBySolicitud(s).orElseGet(() -> {
            CaracterizacionEntity c = new CaracterizacionEntity();
            c.setSolicitud(s);
            return caracterizacionRepo.save(c);
        });
    }

    private Map<String, Object> buildHistory(RegistroBusquedaActiva registro) {
        Map<String, Object> history = new LinkedHashMap<>();
        Integer currentId = registro.getPreguntaActualId();
        if (currentId == null)
            currentId = 1;

        for (int i = 1; i < currentId; i++) {
            QuestionDef def = QUESTIONS.get(i);
            if (def != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", def.id);
                item.put("question", def.text);
                item.put("response", getResponseValue(registro, i));
                history.put("pregunta " + i, item);
            }
        }
        return history;
    }

    private String getResponseValue(RegistroBusquedaActiva r, int qId) {
        PersonaEntity e = r.getEstudiante();
        SolicitudEntity s = r.getSolicitudCupo();

        switch (qId) {
            case 1: return safe(e == null ? null : e.getPrimerNombre());
            case 2: return safe(e == null ? null : e.getSegundoNombre());
            case 3: return safe(e == null ? null : e.getPrimerApellido());
            case 4: return safe(e == null ? null : e.getSegundoApellido());
            case 5: return (e != null && e.getTipoDocumento() != null) ? e.getTipoDocumento().getNombre() : "";
            case 6: return safe(e == null ? null : e.getNumeroDocumento());
            case 10: return (e != null && e.getEtnia() != null) ? e.getEtnia().getNombre() : "";
            case 20: return (s != null && s.isTieneHermano()) ? "Si" : "No";
            case 37: return (r.getTieneNinosSinEstudio() != null && r.getTieneNinosSinEstudio()) ? "Si" : "No";
            default: return "...";
        }
    }

    private String safe(String v) { return v == null ? "" : v; }

    private static class QuestionDef {
        int id;
        String key;
        String text;
        List<String> options;

        QuestionDef(int id, String key, String text, List<String> options) {
            this.id = id;
            this.key = key;
            this.text = text;
            this.options = options;
        }
    }
}
