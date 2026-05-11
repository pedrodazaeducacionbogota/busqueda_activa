package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.*;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Override
    public ByteArrayInputStream generarReportePdf(RegistroBusquedaActiva registro) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Tipografias
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLUE);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Titulo
            Paragraph title = new Paragraph("Reporte de Búsqueda Activa", headFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            String tipoReporte = "Finalizado".equalsIgnoreCase(registro.getEstado()) ? "FINAL" : "PRELIMINAR";
            Paragraph subtitle = new Paragraph("DOCUMENTO " + tipoReporte, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED));
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);
            document.add(Chunk.NEWLINE);

            // Informacion General del Registro
            addSectionTitle(document, "Información del Registro", sectionFont);
            PdfPTable tableGral = new PdfPTable(2);
            tableGral.setWidthPercentage(100);
            addRow(tableGral, "Número de Registro:", registro.getNumeroRegistro(), labelFont, valueFont);
            addRow(tableGral, "Fecha de Registro:", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(registro.getFechaRegistro()), labelFont, valueFont);
            addRow(tableGral, "Estado:", registro.getEstado(), labelFont, valueFont);
            document.add(tableGral);
            document.add(Chunk.NEWLINE);

            // Informacion del Estudiante
            if (registro.getEstudiante() != null) {
                PersonaEntity e = registro.getEstudiante();
                addSectionTitle(document, "Información Sociodemográfica del Estudiante", sectionFont);
                PdfPTable tableEst = new PdfPTable(2);
                tableEst.setWidthPercentage(100);
                addRow(tableEst, "Nombre Completo:", 
                    (e.getPrimerNombre() != null ? e.getPrimerNombre() : "") + " " + 
                    (e.getSegundoNombre() != null ? e.getSegundoNombre() : "") + " " + 
                    (e.getPrimerApellido() != null ? e.getPrimerApellido() : "") + " " + 
                    (e.getSegundoApellido() != null ? e.getSegundoApellido() : ""), labelFont, valueFont);
                addRow(tableEst, "Documento:", (e.getTipoDocumento() != null ? e.getTipoDocumento().getNombre() : "N/A") + " " + (e.getNumeroDocumento() != null ? e.getNumeroDocumento() : ""), labelFont, valueFont);
                addRow(tableEst, "Fecha Nacimiento:", e.getFechaNacimiento() != null ? new SimpleDateFormat("dd/MM/yyyy").format(e.getFechaNacimiento()) : "N/A", labelFont, valueFont);
                addRow(tableEst, "Dirección:", e.getUbicacion() != null ? e.getUbicacion().getDireccion() : "N/A", labelFont, valueFont);
                addRow(tableEst, "País Nacimiento:", e.getPaisNacimiento() != null ? e.getPaisNacimiento().getNombre() : "N/A", labelFont, valueFont);
                document.add(tableEst);
                document.add(Chunk.NEWLINE);
            }

            // Info de Cupo
            if (registro.getSolicitudCupo() != null) {
                SolicitudEntity s = registro.getSolicitudCupo();
                addSectionTitle(document, "Solicitud de Cupo Educativo", sectionFont);
                PdfPTable tableCupo = new PdfPTable(2);
                tableCupo.setWidthPercentage(100);
                addRow(tableCupo, "Último Año Aprobado:", s.getUltimoAnioAprobado() != null ? s.getUltimoAnioAprobado().getNombre() : "N/A", labelFont, valueFont);
                addRow(tableCupo, "Grado Asignado:", s.getGradoSolicitaCupo() != null ? s.getGradoSolicitaCupo().getNombre() : "N/A", labelFont, valueFont);
                addRow(tableCupo, "¿Tiene Hermanos?:", s.isTieneHermano() ? "SÍ" : "NO", labelFont, valueFont);
                if (s.isTieneHermano()) {
                    PersonaEntity h = s.getHermano();
                    if (h != null) {
                        addRow(tableCupo, "Nombre Hermano:", (h.getPrimerNombre() != null ? h.getPrimerNombre() : "") + " " + (h.getPrimerApellido() != null ? h.getPrimerApellido() : ""), labelFont, valueFont);
                    }
                }
                document.add(tableCupo);
                document.add(Chunk.NEWLINE);
            }

            // Info del Responsable
            if (registro.getResponsable() != null) {
                PersonaEntity r = registro.getResponsable();
                addSectionTitle(document, "Información del Responsable / Acudiente", sectionFont);
                PdfPTable tableResp = new PdfPTable(2);
                tableResp.setWidthPercentage(100);
                addRow(tableResp, "Nombre:", r.getPrimerNombre() + " " + r.getPrimerApellido(), labelFont, valueFont);
                addRow(tableResp, "Parentesco:", r.getParentesco() != null ? r.getParentesco().getNombre() : "N/A", labelFont, valueFont);
                addRow(tableResp, "Celular:", r.getCelulares(), labelFont, valueFont);
                addRow(tableResp, "Correo:", r.getEmails(), labelFont, valueFont);
                document.add(tableResp);
                document.add(Chunk.NEWLINE);
            }

            // Factores de Desescolarización
            if (registro.getTieneNinosSinEstudio() != null && registro.getTieneNinosSinEstudio()) {
                addSectionTitle(document, "Factores de Desescolarización (Núcleo Familiar)", sectionFont);
                Paragraph pCant = new Paragraph("Cantidad de niños sin estudio: " + registro.getCantidadNinosSinEstudio(), valueFont);
                document.add(pCant);
                document.add(Chunk.NEWLINE);

                for (FactorDesescolarizacion f : registro.getFactoresDesescolarizacion()) {
                    PdfPTable tableFact = new PdfPTable(2);
                    tableFact.setWidthPercentage(100);
                    addRow(tableFact, "Rango Edad:", f.getRangoEdad(), labelFont, valueFont);
                    addRow(tableFact, "Razón Principal:", f.getRazonPrincipal(), labelFont, valueFont);
                    if (f.getRazonAdicional() != null) {
                        addRow(tableFact, "Razón Adicional:", f.getRazonAdicional(), labelFont, valueFont);
                    }
                    document.add(tableFact);
                    document.add(new Paragraph("--------------------------------------------------"));
                }
            }

            document.close();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addSectionTitle(Document document, String title, Font font) throws DocumentException {
        Paragraph p = new Paragraph(title, font);
        p.setSpacingAfter(5);
        document.add(p);
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        cellValue.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(cellValue);
    }
}
