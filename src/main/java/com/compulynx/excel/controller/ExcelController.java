package com.compulynx.excel.controller;

import com.compulynx.excel.dto.TextResponse;
import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.ExcelService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    /** This method generates an Excel file with the specified number of rows and returns a TextResponse containing the file's content or a message.
     * It uses the ExcelService to create the Excel file and handles any exceptions that may occur during the file generation process.
     *
     * @param numberOfRows The number of rows to include in the generated Excel file.
     * @return A ResponseEntity containing a TextResponse with the generated Excel file's content or a message.
     * @throws IOException If there is an error during file generation or writing the response.
     * @throws InterruptedException If the thread is interrupted during the file generation process.
     */
    @GetMapping
    public ResponseEntity<TextResponse> generateExcelFile(@RequestParam Long numberOfRows ) throws IOException, InterruptedException {
        TextResponse response = new TextResponse();
        response.setText(this.excelService.generateExcelFile(numberOfRows));
        return ResponseEntity.ok(response);
    }

    /** This method reads an uploaded Excel file and returns its content as a map where the key is the row number and the value is a list of cell values for that row.
     * It uses the ExcelService to process the uploaded file and extract its content, handling any exceptions that may occur during file reading.
     *
     * @param multipartFile The uploaded Excel file to be read.
     * @return A ResponseEntity containing a map of row numbers to lists of cell values extracted from the Excel file.
     * @throws IOException If there is an error reading the file or processing its content.
     * @throws OpenXML4JException If there is an error related to the OpenXML format of the Excel file.
     * @throws SAXException If there is an error during XML parsing while reading the Excel file.
     */
    @GetMapping("/read")
    public ResponseEntity<Map<Integer, List<String>>> readExcelFile(@RequestParam("file") MultipartFile multipartFile)
            throws IOException, OpenXML4JException, SAXException {

        return ResponseEntity.ok(this.excelService.readExcelFile(multipartFile));
    }

    /** This method converts an uploaded Excel file to CSV format and returns the result as a TextResponse.
     * It uses the ExcelService to perform the conversion and handles any exceptions that may occur during the process.
     *
     * @param multipartFile The uploaded Excel file to be converted to CSV format.
     * @return A ResponseEntity containing a TextResponse with the converted CSV content.
     * @throws Exception If there is an error during the conversion process.
     */
    @PostMapping("/convertToCsv")
    public ResponseEntity<TextResponse> convertExcelToCsv(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        TextResponse response = new TextResponse();
        response.setText(this.excelService.convertExcelFileToCsv(multipartFile));
        return ResponseEntity.ok(response);
    }

    /** This method exports the student data to an Excel file and returns it as a downloadable response.
     * It sets the appropriate headers for file download and uses the ExcelService to generate the Excel data.
     *
     * @return A ResponseEntity containing the Excel file as a byte array, along with headers for file download.
     * @throws IOException If there is an error during file generation or writing the response.
     */
    @PostMapping("/exportToExcelFile")
    public ResponseEntity<byte[]> exportToExcelFile() throws IOException {

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.xlsx");

        return new ResponseEntity<>(this.excelService.exportExcelFile(), headers, HttpStatus.OK);
     }
}
