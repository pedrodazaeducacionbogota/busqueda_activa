package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.QuestionResponse;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.*;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.repositories.RegistroBusquedaActivaRepository;
import co.gov.educacionbogota.sicobertura.repository.*;
import co.gov.educacionbogota.sicobertura.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionFlowServiceImpl implements QuestionFlowService {

    @Autowired
    private RegistroBusquedaActivaRepository repository;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private REFReferenciaRepository refRepo;

    @Autowired
    private REFPaisRepository paisRepo;

    @Autowired
    private UbicacionRepository ubicacionRepo;

    @Autowired
    private CaracterizacionRepository caracterizacionRepo;

    @Autowired
    private InstitucionRepository institucionRepo;

    @Autowired
    private REFLocalidadRepository localidadRepo;

    private static final Map<Integer, QuestionDef> QUESTIONS = new LinkedHashMap<>();

    static {
        // --- ETAPA 1: ESTUDIANTE ---
        addQuestion(1, "Primer Nombre del Estudiante", null);
        addQuestion(2, "Segundo Nombre del Estudiante", null);
        addQuestion(3, "Primer Apellido del Estudiante", null);
        addQuestion(4, "Segundo Apellido del Estudiante", null);
        addQuestion(5, "Tipo de Documento", Arrays.asList("RC", "TI", "CC", "CE", "NES", "PEP", "PPT", "VISA"));
        addQuestion(6, "Número de Documento", null);
        addQuestion(7, "Fecha de Nacimiento (yyyy-MM-dd)", null);
        addQuestion(8, "Sexo", Arrays.asList("Femenino", "Masculino"));
        addQuestion(9, "¿La estudiante es gestante?", Arrays.asList("Sí", "No"));
        addQuestion(10, "Etnia", Arrays.asList("Indígena", "Afrodescendiente", "Raizal", "Roms", "Ninguna"));
        addQuestion(11, "Tipo de Discapacidad", Arrays.asList("Física", "Auditiva", "Visual", "Sordoceguera",
                "Intelectual", "Psicosocial", "Múltiple", "Ninguna"));
        addQuestion(12, "¿Se reconoce como parte de alguna población (víctima, desplazado, etc)?",
                Arrays.asList("Sí", "No"));
        addQuestion(13, "Localidad de Residencia",
                Arrays.asList("Usaquén", "Chapinero", "Santa Fe", "San Cristóbal", "Usme", "Tunjuelito", "Bosa",
                        "Kennedy", "Fontibón", "Engativá", "Suba", "Barrios Unidos", "Teusaquillo", "Los Mártires",
                        "Antonio Nariño", "Puente Aranda", "La Candelaria", "Rafael Uribe Uribe", "Ciudad Bolívar",
                        "Sumapaz"));
        addQuestion(14, "Barrio", null);
        addQuestion(15, "Dirección de Vivienda", null);

        // --- ETAPA 2: CUPO ---
        addQuestion(16, "Último Año Aprobado",
                Arrays.asList("Preescolar", "1º", "2º", "3º", "4º", "5º", "6º", "7º", "8º", "9º", "10º", "11º"));
        addQuestion(17, "Localidad de la Institución de interés", null);
        addQuestion(18, "Nombre de las Instituciones", null);
        addQuestion(19, "Grado Asignado", null);
        addQuestion(20, "¿El Estudiante tiene hermanos en una institución oficial?", Arrays.asList("Sí", "No"));

        // Ramificación Azul (Hermanos)
        addQuestion(21, "Tipo de Documento del Hermano",
                Arrays.asList("RC", "TI", "CC", "CE", "NES", "PEP", "PPT", "VISA"));
        addQuestion(22, "Número de Documento del Hermano", null);
        addQuestion(23, "Nombres y Apellidos del Hermano", null);
        addQuestion(24, "¿Requiere que el estudiante esté en la misma institución?", Arrays.asList("Sí", "No"));
        addQuestion(25, "Nombre de la Institución del Hermano", null);

        // --- ETAPA 3: RESPONSABLE ---
        addQuestion(26, "Tipo de Documento del Responsable", Arrays.asList("CC", "CE", "PEP", "PPT", "VISA"));
        addQuestion(27, "Número de Documento del Responsable", null);
        addQuestion(28, "Nombres y Apellidos del Responsable", null);
        addQuestion(29, "Correo Electrónico", null);
        addQuestion(30, "Número de Contacto", null);
        addQuestion(31, "Parentesco", Arrays.asList("Padre", "Madre", "Abuelo/a", "Tío/a", "Hermano/a", "Cuidador"));
        addQuestion(32, "Nivel de Escolaridad", Arrays.asList("Primaria", "Bachillerato", "Técnico/Tecnólogo",
                "Universitario", "Postgrado", "Ninguno"));
        addQuestion(33, "Ocupación", null);
        addQuestion(34, "Localidad Responsable", null);
        addQuestion(35, "Barrio Responsable", null);
        addQuestion(36, "Dirección Responsable", null);

        // --- ETAPA 4: FACTORES DESESCOLARIZACIÓN (PÚRPURA) ---
        addQuestion(37,
                "¿En su núcleo familiar existen niños, niñas, adolescentes y/o jóvenes que no se encuentren estudiando?",
                Arrays.asList("Sí", "No"));
        addQuestion(38, "¿Cuántos niños conoce que no están estudiando?", null);
        addQuestion(39, "Rango de edad de las personas que no están estudiando",
                Arrays.asList("3-5 años", "6-11 años", "12-15 años", "16-17 años", "18+ años"));
        addQuestion(40, "¿Cuáles son las razones por las que no están estudiando?", null);
    }

    private static void addQuestion(int id, String text, List<String> options) {
        QUESTIONS.put(id, new QuestionDef(id, text, options));
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

        // Generación de PDF según hitos
        if (nextId == 26) { // Terminó sección Cupo (Azul)
            reporteService.generarReportePdf(registro); // Preliminary
        } else if (nextId == -1) { // Terminó todo
            registro.setEstado("Finalizado");
            repository.save(registro);
            reporteService.generarReportePdf(registro); // Final
        }

        return buildResponse(registro, nextId);
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
        // Lógica de Ramificación
        if (currentId == 20) { // ¿Tiene hermanos?
            if ("Sí".equalsIgnoreCase(answer))
                return 21;
            return 26; // Saltear a Responsable
        }
        if (currentId == 25)
            return 26; // Vuelve de rama hermanos

        if (currentId == 37) { // ¿Niños sin estudio?
            if ("Sí".equalsIgnoreCase(answer))
                return 38;
            return -1; // Fin
        }
        if (currentId == 40)
            return -1; // Fin

        // Secuencial por defecto
        int nextId = currentId + 1;
        return QUESTIONS.containsKey(nextId) ? nextId : -1;
    }

    private void saveAnswerToEntity(RegistroBusquedaActiva registro, Integer qId, String ans) {
        // Inicializar entidades si son nulas
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
            case 5: e.setTipoDocumento(findRef("TIPO_DOCUMENTO", ans)); break;
            case 6: e.setNumeroDocumento(ans); break;
            case 7:
                try { e.setFechaNacimiento(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(ans)); } catch (Exception ex) {}
                break;
            case 8: e.setSexo(findRef("SEXO", ans)); break;
            case 9: e.setGestante("Sí".equalsIgnoreCase(ans)); break;
            case 10: e.setEtnia(findRef("ETNIA", ans)); break;
            case 11: e.setTipoDiscapacidad(findRef("TIPO_DISCAPACIDAD", ans)); break;
            case 12: e.setPoblacionDiferencial(ans); break;
            case 13:
                registro.setLocalidad(ans);
                getOrCreateCaracterizacion(s).setLocalidadInstitucion(findLocalidad(ans));
                break;
            case 14: registro.setBarrio(ans); break;
            case 15:
                if (e.getUbicacion() == null) e.setUbicacion(new UbicacionEntity());
                e.getUbicacion().setDireccion(ans);
                break;

            case 16: s.setUltimoAnioAprobado(findRef("GRADO", ans)); break;
            case 17: getOrCreateCaracterizacion(s).setLocalidadInstitucion(findLocalidad(ans)); break;
            case 18: getOrCreateCaracterizacion(s).setInstitucion(findInstitucion(ans)); break;
            case 19: s.setGradoSolicitaCupo(findRef("GRADO", ans)); break;
            case 20: s.setTieneHermano("Sí".equalsIgnoreCase(ans)); break;
            case 21: /* e.setTipoDocumento(findRef("TIPO_DOCUMENTO", ans)); */ break;
            case 22: s.setPerIdHermano(ans); break;
            case 23:
                if (s.getHermano() == null) s.setHermano(new PersonaEntity());
                s.getHermano().setPrimerNombre(ans);
                break;
            case 24: getOrCreateCaracterizacion(s).setMismaInstitucion("Sí".equalsIgnoreCase(ans)); break;
            case 25: getOrCreateCaracterizacion(s).setInstitucionHermano(findInstitucion(ans)); break;

            case 26: r.setTipoDocumento(findRef("TIPO_DOCUMENTO", ans)); break;
            case 27: r.setNumeroDocumento(ans); break;
            case 28: r.setPrimerNombre(ans); break;
            case 29: r.setPrimerApellido(ans); break;
            case 30: r.setCelulares(ans); break;
            case 31: r.setParentesco(findRef("PARENTESCO", ans)); break;
            case 32: r.setNivelEscolaridad(findRef("NIVEL_ESCOLARIDAD", ans)); break;
            case 33: r.setOcupacion(findRef("OCUPACION", ans)); break;

            case 37:
                registro.setTieneNinosSinEstudio("Sí".equalsIgnoreCase(ans));
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
        }
    }

    private REFReferenciaEntity findRef(String tipo, String val) {
        return refRepo.findFirstByTipoAndValorTxt(tipo, val).orElse(null);
    }

    private REFLocalidadEntity findLocalidad(String name) {
        return localidadRepo.findByNombre(name).stream().findFirst().orElse(null);
    }

    private InstitucionEntity findInstitucion(String name) {
        return institucionRepo.findByNombreContainingIgnoreCaseAndActivaTrue(name).orElse(null);
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
        PersonaEntity resp = r.getResponsable();

        switch (qId) {
            case 1:
                String val1 = (e != null) ? e.getPrimerNombre() : "";
                return val1 != null ? val1 : "";
            case 2:
                String val2 = (e != null) ? e.getSegundoNombre() : "";
                return val2 != null ? val2 : "";
            case 3:
                String val3 = (e != null) ? e.getPrimerApellido() : "";
                return val3 != null ? val3 : "";
            case 4:
                String val4 = (e != null) ? e.getSegundoApellido() : "";
                return val4 != null ? val4 : "";
            case 5:
                return (e != null && e.getTipoDocumento() != null) ? e.getTipoDocumento().getNombre() : "";
            case 6:
                String val6 = (e != null) ? e.getNumeroDocumento() : "";
                return val6 != null ? val6 : "";
            case 10:
                return (e != null && e.getEtnia() != null) ? e.getEtnia().getNombre() : "";
            case 20:
                return (s != null && s.isTieneHermano()) ? "Sí" : "No";
            case 37:
                return (r.getTieneNinosSinEstudio() != null && r.getTieneNinosSinEstudio()) ? "Sí" : "No";
            default:
                return "...";
        }
    }

    private static class QuestionDef {
        int id;
        String text;
        List<String> options;

        QuestionDef(int id, String text, List<String> options) {
            this.id = id;
            this.text = text;
            this.options = options;
        }
    }
}
