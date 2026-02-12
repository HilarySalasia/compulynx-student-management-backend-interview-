package com.compulynx.excel.controller;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.CSVService;
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

    @PostMapping("/uploadStudentsData")
    public List<Student> uploadStudentsData(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return this.csvService.readAndSaveCsvFile(multipartFile);
    }

    @PostMapping("/exportToCsvFile")
    public ResponseEntity<byte[]> exportToCsvFile(@RequestBody List<Student> data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.pdf");

        return new ResponseEntity<>(this.csvService.exportCSVFile(data), headers, HttpStatus.OK);
    }

    @PostMapping("/exportToPdfFile")
    public ResponseEntity<byte[]> exportToPdfFile(@RequestBody List<Student> data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.pdf");

        return new ResponseEntity<>(this.csvService.exportPdfFile(data), headers, HttpStatus.OK);
    }
}
