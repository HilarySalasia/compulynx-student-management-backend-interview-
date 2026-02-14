package com.compulynx.excel.controller;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.CSVService;
import com.compulynx.excel.service.PdfConverterService;
import com.compulynx.excel.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CSVController {

    private final CSVService csvService;
    private final PdfConverterService pdfConverterService;
    private final StudentService studentService;

    /**
     * This method reads a CSV file, saves the data to the database, and returns the list of students.
     * It uses the CSVService to handle the file processing and database interaction.
     *
     * @param multipartFile The uploaded CSV file containing student data.
     * @return A list of Student objects that were saved to the database.
     * @throws IOException If there is an error reading the file or saving data to the database.
     */
    @PostMapping("/uploadStudentsData")
    public List<Student> uploadStudentsData(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return this.csvService.readAndSaveCsvFile(multipartFile);
    }

    /** This method exports the student data to a CSV file and returns it as a downloadable response.
     * It sets the appropriate headers for file download and uses the CSVService to generate the CSV data.
     *
     * @return A ResponseEntity containing the CSV file as a byte array, along with headers for file download.
     */
    @PostMapping("/exportToCsvFile")
    public ResponseEntity<byte[]> exportToCsvFile() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.pdf");

        return new ResponseEntity<>(this.csvService.exportCSVFile(), headers, HttpStatus.OK);
    }

    /** This method exports the student data to a PDF file and writes it directly to the HTTP response output stream.
     * It sets the content type and headers for file download, retrieves the student data, and uses the PdfConverterService
     * to convert the data to PDF format and write it to the response.
     *
     * @param response The HttpServletResponse object used to set headers and write the PDF data.
     * @throws IOException If there is an error writing the PDF data to the response output stream.
     */
    @PostMapping("/exportToPdfFile")
    public void exportToPdfFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=students.pdf");

        // Pull from database via a Stream or Iterator to avoid Loading the whole List!
        Iterable<Student> students = studentService.getAllStudents();

        pdfConverterService.convertLargeDataToPdf(students, response.getOutputStream());
    }
}
