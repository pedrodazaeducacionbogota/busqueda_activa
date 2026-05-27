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
import co.gov.educacionbogota.sicobertura.entities.SolicitudColegioEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import co.gov.educacionbogota.sicobertura.exception.ReglaNegocioException;
import co.gov.educacionbogota.sicobertura.repository.SolicitudColegioRepository;

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
 * PDF resumen formulario BA. iText 7. Renderiza 4 etapas + estado FINAL/PRELIMINAR
 * según `finalizado`. Encoded base64 en response.
 */
@Service
public class BAPdfService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired private BAFormularioService formularioService;
    @Autowired private SolicitudColegioRepository solicitudColegioRepository;

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
        construirPdf(f, out);
        return out.toByteArray();
    }

    private void construirPdf(BusquedaActivaFormularioEntity f, ByteArrayOutputStream out) {
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
            row(h, "Etapa config:", str(f.getEtapa()));
            row(h, "Fecha creación:", f.getFechaCrea() != null ? f.getFechaCrea().format(FECHA_FMT) : "");
            row(h, "Estado:", tipo);
            row(h, "Última etapa:", str(f.getUltimaEtapa()));
            if (f.getProfesional() != null) {
                row(h, "Profesional:", f.getProfesional().getNombreUsuario());
            }
            doc.add(h);

            // Etapa 1: estudiante
            seccion(doc, "Etapa 1: Estudiante");
            if (f.getEstudiante() != null) {
                PersonaEntity p = f.getEstudiante().getPersona();
                doc.add(personaTabla(p));
                Table e1 = tabla();
                row(e1, "País nacimiento:", refDesc(p != null ? p.getPaisNacimiento() : null));
                row(e1, "Edad:", f.getEstudiante().getEdadTxt());
                row(e1, "Rango edad:", refDesc(f.getEstudiante().getRangoEdad()));
                if (p != null) {
                    row(e1, "Etnia:", p.getEtnia() != null ? refDesc(p.getEtnia()) : p.getEtniaOtro());
                    row(e1, "Población:", p.getPoblacionDiferencial() != null
                            ? refDesc(p.getPoblacionDiferencial()) : p.getPoblacionOtro());
                    row(e1, "Discapacidad:", String.valueOf(Boolean.TRUE.equals(p.getDiscapacidad())));
                    if (Boolean.TRUE.equals(p.getDiscapacidad())) row(e1, "Tipo discapacidad:", refDesc(p.getTipoDiscapacidad()));
                    row(e1, "Gestante:", String.valueOf(p.isGestante()));
                    ubicacionRows(e1, p.getUbicacion());
                }
                doc.add(e1);
            } else {
                doc.add(new Paragraph("Sin estudiante").setFontSize(9));
            }

            // Etapa 2: solicitud cupo + hermanos + colegios
            seccion(doc, "Etapa 2: Solicitud de Cupo");
            SolicitudEntity sol = f.getSolicitud();
            if (sol != null) {
                Table e2 = tabla();
                row(e2, "Último año aprobado:", refDesc(sol.getUltimoAnioAprobado()));
                row(e2, "Grado solicita cupo:", refDesc(sol.getGradoSolicitaCupo()));
                row(e2, "Tiene hermano:", String.valueOf(sol.isTieneHermano()));
                doc.add(e2);
                if (sol.isTieneHermano() && sol.getHermano() != null) {
                    seccion2sub(doc, "Hermano");
                    doc.add(personaTabla(sol.getHermano()));
                    Table eh = tabla();
                    row(eh, "Misma institución:", String.valueOf(f.isMismaInstitucionHermano()));
                    if (f.getInstitucionHermano() != null) row(eh, "Institución hermano:", f.getInstitucionHermano().getNombre());
                    doc.add(eh);
                }
                // Colegios en orden de preferencia
                java.util.List<SolicitudColegioEntity> colegios =
                        solicitudColegioRepository.findBySolicitud_IdOrderByOrdenPreferencia(sol.getId());
                if (!colegios.isEmpty()) {
                    Table tc = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                            .setWidth(UnitValue.createPercentValue(100));
                    tc.addHeaderCell(headerCell("Pref."));
                    tc.addHeaderCell(headerCell("Institución"));
                    for (SolicitudColegioEntity sc : colegios) {
                        tc.addCell(cell(str(sc.getOrdenPreferencia())));
                        tc.addCell(cell(sc.getColegio() != null ? sc.getColegio().getNombre() : ""));
                    }
                    doc.add(tc);
                }
            } else {
                doc.add(new Paragraph("Sin solicitud").setFontSize(9));
            }

            // Etapa 3: acudiente
            seccion(doc, "Etapa 3: Responsable / Acudiente");
            if (f.getAcudiente() != null) {
                PersonaEntity a = f.getAcudiente();
                doc.add(personaTabla(a));
                Table e3 = tabla();
                ubicacionRows(e3, a.getUbicacion());
                doc.add(e3);
            } else {
                doc.add(new Paragraph("Sin acudiente").setFontSize(9));
            }

            // Etapa 4: factores descolarización
            seccion(doc, "Etapa 4: Factores de Descolarización");
            if (f.getNoEstudiandoRangos() != null && !f.getNoEstudiandoRangos().isEmpty()) {
                Table e4 = new Table(UnitValue.createPercentArray(new float[]{2, 1, 3, 3}))
                        .setWidth(UnitValue.createPercentValue(100));
                e4.addHeaderCell(headerCell("Rango"));
                e4.addHeaderCell(headerCell("Cuántos"));
                e4.addHeaderCell(headerCell("Razón"));
                e4.addHeaderCell(headerCell("Sub-razón"));
                for (BANoEstudiandoEntity r : f.getNoEstudiandoRangos()) {
                    e4.addCell(cell(r.getRangoEdadCodigo()));
                    e4.addCell(cell(String.valueOf(r.getCuantos())));
                    e4.addCell(cell(refDesc(r.getRazon())));
                    e4.addCell(cell(refDesc(r.getRazonOtra())));
                }
                doc.add(e4);
            } else {
                doc.add(new Paragraph("No hay NNAJ no estudiando reportados").setFontSize(9));
            }
        } catch (Exception ex) {
            throw new ReglaNegocioException("Error generando PDF: " + ex.getMessage());
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
        if (p.getCelulares() != null) row(t, "Celulares:", p.getCelulares());
        if (p.getEmails() != null) row(t, "Emails:", p.getEmails());
        if (p.getFechaNacimiento() != null) {
            row(t, "Fecha nac:", new SimpleDateFormat("yyyy-MM-dd").format(p.getFechaNacimiento()));
        }
        if (p.getSexo() != null) row(t, "Sexo:", refDesc(p.getSexo()));
        return t;
    }

    private void ubicacionRows(Table t, co.gov.educacionbogota.sicobertura.entities.UbicacionEntity u) {
        if (u == null) return;
        row(t, "Localidad:", refDesc(u.getLocalidad()));
        row(t, "Barrio:", u.getBarrio() != null ? refDesc(u.getBarrio()) : u.getBarrioOtro());
        row(t, "Dirección:", u.getDireccion());
        row(t, "Complemento:", u.getDireccionComplemento());
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

    private void seccion2sub(Document doc, String titulo) {
        doc.add(new Paragraph(titulo).setFontSize(11).setBold()
                .setFontColor(ColorConstants.GRAY).setMarginTop(4));
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
        return r != null ? r.getNombre() : "";
    }

    private String str(Object o) {
        return o != null ? String.valueOf(o) : "";
    }
}
