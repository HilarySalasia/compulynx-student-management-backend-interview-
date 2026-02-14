package com.compulynx.excel.service.impl;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.PdfConverterService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

@Configuration
class PdfConverterServiceImpl implements PdfConverterService {
    @Value("${server.port}")
    public String servicePort;

    /**
     * Converts HTML content to PDF format and returns it as a ByteArrayOutputStream.
     *
     * @param html The HTML content to be converted to PDF.
     * @return A ByteArrayOutputStream containing the generated PDF data.
     */
    @Override
    public ByteArrayOutputStream convert(String html) {
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:" + servicePort);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        com.itextpdf.html2pdf.HtmlConverter.convertToPdf(html, outputStream, converterProperties);
        return outputStream;
    }

    /**
     * Converts a large dataset of Student objects into PDF format and writes it to the provided OutputStream.
     *
     * @param students An iterable collection of Student objects to be included in the PDF.
     * @param os       The OutputStream where the generated PDF will be written.
     */
    public void convertLargeDataToPdf(Iterable<Student> students, OutputStream os) {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Create a table with specific column widths
        Table table = new Table(new float[]{1, 3, 3, 3, 3, 2});
        table.addHeaderCell("ID");
        table.addHeaderCell("First Name");
        table.addHeaderCell("Last Name");
        table.addHeaderCell("DOB");
        table.addHeaderCell("Class");
        table.addHeaderCell("Score");

        int count = 0;
        for (Student s : students) {
            table.addCell(String.valueOf(count + 1));
            table.addCell(s.getFirstName());
            table.addCell(s.getLastName());
            table.addCell(s.getDob().toString());
            table.addCell(s.getClassName());
            table.addCell(String.valueOf(s.getScore()));

            if (++count % 1000 == 0) {
                document.add(table);
            }
        }

        document.add(table);
        document.close();
    }
}