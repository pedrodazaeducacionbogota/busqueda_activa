package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.gov.educacionbogota.sicobertura.busquedaactiva.dtos.ResumenPdfDto;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BANoEstudiandoEntity;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.BusquedaActivaFormularioEntity;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.RefListadoRepository;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

/**
 * PDF resumen formulario BA. iText 7. Renderiza 7 secciones + estado FINAL/PRELIMINAR
 * según `finalizado`. Encoded base64 en response (mismo contrato legacy).
 */
@Service
public class BAPdfService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired private BAFormularioService formularioService;
    @Autowired private RefListadoRepository refRepo;

    @Transactional(readOnly = true)
    public ResumenPdfDto generarResumen(Long id) {
        BusquedaActivaFormularioEntity f = formularioService.getFormulario(id);
        byte[] bytes = generarPdf(f);
        ResumenPdfDto dto = new ResumenPdfDto();
        dto.setEstado(true);
        dto.setIdSolicitud(id);
        dto.setBase64(Base64.getEncoder().encodeToString(bytes));
        return dto;
    }

    private byte[] generarPdf(BusquedaActivaFormularioEntity f) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Resumen Búsqueda Activa")
                    .setFontSize(18).setBold()
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            String tipo = f.isFinalizado() ? "FINAL" : "PRELIMINAR";
            doc.add(new Paragraph("DOCUMENTO " + tipo)
                    .setFontSize(12).setBold()
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(""));

            // Encabezado
            seccion(doc, "Información General");
            Table h = tabla();
            row(h, "ID Formulario:", str(f.getId()));
            row(h, "Vigencia:", str(f.getVigencia()));
            row(h, "Etapa:", str(f.getEtapa()));
            row(h, "Fecha creación:", f.getFechaCrea() != null ? f.getFechaCrea().format(FECHA_FMT) : "");
            row(h, "Estado:", tipo);
            row(h, "Última sección:", str(f.getUltimaSeccion()));
            if (f.getProfesional() != null) {
                row(h, "Profesional:", f.getProfesional().getNombreUsuario());
            }
            doc.add(h);

            // Sección 1
            seccion(doc, "Sección 1: Actividad / Ubicación visita");
            Table s1 = tabla();
            row(s1, "Actividad:", refDesc(f.getActividad()));
            row(s1, "Actividad otra:", f.getActividadOtra());
            row(s1, "Evento / feria:", f.getNombreEventoFeria());
            row(s1, "Población:", f.getPoblacionEvento());
            if (f.getUbicacion() != null) {
                row(s1, "Localidad:", refDesc(f.getUbicacion().getLocalidad()));
                row(s1, "Barrio:", f.getUbicacion().getBarrio() != null
                        ? refDesc(f.getUbicacion().getBarrio()) : f.getUbicacion().getBarrioOtro());
            }
            doc.add(s1);

            // Sección 2
            seccion(doc, "Sección 2: Atiende Visita");
            doc.add(personaTabla(f.getAtiendeVisita()));

            // Sección 3
            seccion(doc, "Sección 3: Colegios / Jardines Cerca");
            Table s3 = tabla();
            row(s3, "Colegios cerca:", String.valueOf(f.isColegiosCerca()));
            if (f.getIdeColegioCerca() != null) row(s3, "Sede colegio:", f.getIdeColegioCerca().getNombre());
            row(s3, "Colegio cual:", f.getColegioCercaCual());
            row(s3, "Jardines cerca:", String.valueOf(f.isJardinesCerca()));
            if (f.getIdeJardinCerca() != null) row(s3, "Sede jardín:", f.getIdeJardinCerca().getNombre());
            row(s3, "Jardín cual:", f.getJardinCercaCual());
            doc.add(s3);

            // Sección 4
            seccion(doc, "Sección 4: No Estudiando por Rango Edad");
            if (f.getNoEstudiandoRangos() != null && !f.getNoEstudiandoRangos().isEmpty()) {
                Table s4 = new Table(UnitValue.createPercentArray(new float[]{1, 1, 2, 2}))
                        .setWidth(UnitValue.createPercentValue(100));
                s4.addHeaderCell(headerCell("Rango"));
                s4.addHeaderCell(headerCell("Cuántos"));
                s4.addHeaderCell(headerCell("Razón"));
                s4.addHeaderCell(headerCell("Razón otra"));
                for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
                    s4.addCell(cell(r.getRangoEdadCodigo()));
                    s4.addCell(cell(String.valueOf(r.getCuantos())));
                    s4.addCell(cell(refDesc(r.getRazon())));
                    s4.addCell(cell(r.getRazonOtra()));
                }
                doc.add(s4);
            } else {
                doc.add(new Paragraph("Sin datos").setFontSize(9));
            }

            // Sección 5
            seccion(doc, "Sección 5: Acudiente");
            Table s5 = tabla();
            row(s5, "Atiende es acudiente:", String.valueOf(f.isAtiendeVisitaAcudiente()));
            doc.add(s5);
            doc.add(personaTabla(f.getAcudiente()));

            // Sección 6
            seccion(doc, "Sección 6: Estudiante");
            if (f.getEstudiante() != null) {
                doc.add(personaTabla(f.getEstudiante().getPersona()));
                Table s6e = tabla();
                row(s6e, "Edad:", f.getEstudiante().getEdadTxt());
                row(s6e, "Rango edad:", refDesc(f.getEstudiante().getRangoEdad()));
                row(s6e, "Enfoque diferencial:", refDesc(f.getEstudiante().getEnfoqueDiferencial()));
                doc.add(s6e);
            } else {
                doc.add(new Paragraph("Sin estudiante").setFontSize(9));
            }

            // Sección 7
            seccion(doc, "Sección 7: Educativo / Solicitud Cupo");
            Table s7 = tabla();
            row(s7, "Último año estudio:", refDesc(f.getUltimoAnioEstudio()));
            row(s7, "Repitió último año:", String.valueOf(f.isRepitioUltimoAnio()));
            row(s7, "Veces repitió:", refDesc(f.getVecesRepitioAnio()));
            row(s7, "Último año aprobado:", refDesc(f.getUltimoAnioAprobado()));
            if (f.getIdeSolicitaCupo() != null) row(s7, "Sede solicita cupo:", f.getIdeSolicitaCupo().getNombre());
            row(s7, "Grado solicita cupo:", refDesc(f.getGradoSolicitaCupo()));
            doc.add(s7);

            return out.toByteArray();
        } catch (Exception ex) {
            throw new ReglaNegocioException("Error generando PDF: " + ex.getMessage());
        } finally {
            try { out.close(); } catch (Exception ignore) {}
        }
    }

    // ---- helpers iText ----

    private Table personaTabla(PersonaEntity p) {
        Table t = tabla();
        if (p == null) {
            row(t, "Persona:", "Sin datos");
            return t;
        }
        if (p.getTipoDocumento() != null) row(t, "Tipo doc:", refDesc(p.getTipoDocumento()));
        row(t, "Documento:", p.getNumeroDocumento());
        row(t, "Nombre completo:", joinNombre(p));
        row(t, "Celulares:", p.getCelulares());
        row(t, "Emails:", p.getEmails());
        if (p.getFechaNacimiento() != null) {
            row(t, "Fecha nac:", new SimpleDateFormat("yyyy-MM-dd").format(p.getFechaNacimiento()));
        }
        if (p.getSexo() != null) row(t, "Sexo:", refDesc(p.getSexo()));
        return t;
    }

    private String joinNombre(PersonaEntity p) {
        StringBuilder sb = new StringBuilder();
        if (p.getPrimerNombre() != null) sb.append(p.getPrimerNombre()).append(" ");
        if (p.getSegundoNombre() != null) sb.append(p.getSegundoNombre()).append(" ");
        if (p.getPrimerApellido() != null) sb.append(p.getPrimerApellido()).append(" ");
        if (p.getSegundoApellido() != null) sb.append(p.getSegundoApellido());
        return sb.toString().trim();
    }

    private void seccion(Document doc, String titulo) {
        doc.add(new Paragraph(titulo).setFontSize(13).setBold()
                .setFontColor(ColorConstants.DARK_GRAY).setMarginTop(10));
    }

    private Table tabla() {
        return new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));
    }

    private void row(Table t, String label, String value) {
        t.addCell(new Cell().add(new Paragraph(label).setBold()).setBorder(Border.NO_BORDER));
        t.addCell(new Cell().add(new Paragraph(value != null ? value : "")).setBorder(Border.NO_BORDER));
    }

    private Cell headerCell(String label) {
        return new Cell().add(new Paragraph(label).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY);
    }

    private Cell cell(String value) {
        return new Cell().add(new Paragraph(value != null ? value : ""));
    }

    private String refDesc(RefListado r) {
        return r != null ? r.getDescripcion() : "";
    }

    @SuppressWarnings("unused")
    private String refDescById(Long id) {
        if (id == null) return "";
        return refRepo.findById(id).map(RefListado::getDescripcion).orElse("");
    }

    private String str(Object o) {
        return o != null ? String.valueOf(o) : "";
    }
}
