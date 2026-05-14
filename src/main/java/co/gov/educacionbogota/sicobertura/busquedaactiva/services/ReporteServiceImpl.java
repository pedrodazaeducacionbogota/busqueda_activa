package co.gov.educacionbogota.sicobertura.busquedaactiva.services;

import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.FactorDesescolarizacion;
import co.gov.educacionbogota.sicobertura.busquedaactiva.entities.RegistroBusquedaActiva;
import co.gov.educacionbogota.sicobertura.entities.PersonaEntity;
import co.gov.educacionbogota.sicobertura.entities.RefListado;
import co.gov.educacionbogota.sicobertura.entities.SolicitudEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private RefListadoRepository refListadoRepository;

    @Override
    public ByteArrayInputStream generarReportePdf(RegistroBusquedaActiva registro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            Paragraph title = new Paragraph("Reporte de Busqueda Activa")
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            String tipoReporte = "Finalizado".equalsIgnoreCase(registro.getEstado()) ? "FINAL" : "PRELIMINAR";
            Paragraph subtitle = new Paragraph("DOCUMENTO " + tipoReporte)
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitle);
            document.add(new Paragraph(""));

            // Informacion General del Registro
            addSectionTitle(document, "Informacion del Registro");
            Table tableGral = newTwoColTable();
            addRow(tableGral, "Numero de Registro:", registro.getNumeroRegistro());
            addRow(tableGral, "Fecha de Registro:",
                    registro.getFechaRegistro() != null
                            ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(registro.getFechaRegistro())
                            : "N/A");
            addRow(tableGral, "Estado:", registro.getEstado());
            document.add(tableGral);
            document.add(new Paragraph(""));

            // Informacion del Estudiante
            if (registro.getEstudiante() != null) {
                PersonaEntity e = registro.getEstudiante();
                addSectionTitle(document, "Informacion Sociodemografica del Estudiante");
                Table tableEst = newTwoColTable();
                addRow(tableEst, "Nombre Completo:",
                        join(e.getPrimerNombre(), e.getSegundoNombre(), e.getPrimerApellido(), e.getSegundoApellido()));
                addRow(tableEst, "Documento:",
                        (e.getTipoDocumento() != null ? e.getTipoDocumento().getNombre() : "N/A") + " " + safe(e.getNumeroDocumento()));
                addRow(tableEst, "Fecha Nacimiento:",
                        e.getFechaNacimiento() != null
                                ? new SimpleDateFormat("dd/MM/yyyy").format(e.getFechaNacimiento())
                                : "N/A");
                addRow(tableEst, "Direccion:", e.getUbicacion() != null ? e.getUbicacion().getDireccion() : "N/A");
                addRow(tableEst, "Pais Nacimiento:", e.getPaisNacimiento() != null ? e.getPaisNacimiento().getNombre() : "N/A");
                document.add(tableEst);
                document.add(new Paragraph(""));
            }

            // Solicitud de Cupo
            if (registro.getSolicitudCupo() != null) {
                SolicitudEntity s = registro.getSolicitudCupo();
                addSectionTitle(document, "Solicitud de Cupo Educativo");
                Table tableCupo = newTwoColTable();
                addRow(tableCupo, "Ultimo Anio Aprobado:", s.getUltimoAnioAprobado() != null ? s.getUltimoAnioAprobado().getNombre() : "N/A");
                addRow(tableCupo, "Grado Solicitado:", s.getGradoSolicitaCupo() != null ? s.getGradoSolicitaCupo().getNombre() : "N/A");
                addRow(tableCupo, "Tiene Hermanos:", s.isTieneHermano() ? "SI" : "NO");
                if (s.isTieneHermano() && s.getHermano() != null) {
                    PersonaEntity h = s.getHermano();
                    addRow(tableCupo, "Nombre Hermano:",
                            join(h.getPrimerNombre(), h.getPrimerApellido()));
                }
                document.add(tableCupo);
                document.add(new Paragraph(""));
            }

            // Responsable
            if (registro.getResponsable() != null) {
                PersonaEntity r = registro.getResponsable();
                addSectionTitle(document, "Informacion del Responsable / Acudiente");
                Table tableResp = newTwoColTable();
                addRow(tableResp, "Nombre:", join(r.getPrimerNombre(), r.getPrimerApellido()));
                addRow(tableResp, "Parentesco:", resolveRefNombre(r.getIdParentesco()));
                addRow(tableResp, "Celular:", safe(r.getCelulares()));
                addRow(tableResp, "Correo:", safe(r.getEmails()));
                document.add(tableResp);
                document.add(new Paragraph(""));
            }

            // Factores Desescolarizacion
            if (Boolean.TRUE.equals(registro.getTieneNinosSinEstudio())) {
                addSectionTitle(document, "Factores de Desescolarizacion (Nucleo Familiar)");
                document.add(new Paragraph("Cantidad de ninos sin estudio: "
                        + (registro.getCantidadNinosSinEstudio() != null ? registro.getCantidadNinosSinEstudio() : 0)));

                if (registro.getFactoresDesescolarizacion() != null) {
                    for (FactorDesescolarizacion f : registro.getFactoresDesescolarizacion()) {
                        Table tableFact = newTwoColTable();
                        addRow(tableFact, "Rango Edad:", safe(f.getRangoEdad()));
                        addRow(tableFact, "Razon Principal:", safe(f.getRazonPrincipal()));
                        if (f.getRazonAdicional() != null) {
                            addRow(tableFact, "Razon Adicional:", f.getRazonAdicional());
                        }
                        document.add(tableFact);
                        document.add(new Paragraph("--------------------------------------------------"));
                    }
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error generando reporte PDF: " + ex.getMessage(), ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String resolveRefNombre(BigInteger idRefListado) {
        if (idRefListado == null) return "N/A";
        return refListadoRepository.findById(idRefListado.longValue())
                .map(RefListado::getNombre)
                .orElse("N/A");
    }

    private Table newTwoColTable() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{30f, 70f}));
        t.setWidth(UnitValue.createPercentValue(100));
        return t;
    }

    private void addSectionTitle(Document document, String title) {
        Paragraph p = new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        document.add(p);
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "N/A")).setBorder(Border.NO_BORDER));
    }

    private String safe(String v) { return v == null ? "" : v; }

    private String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(p);
            }
        }
        return sb.length() == 0 ? "N/A" : sb.toString();
    }
}
