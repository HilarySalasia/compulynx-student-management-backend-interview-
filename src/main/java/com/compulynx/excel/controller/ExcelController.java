package com.compulynx.excel.controller;

import com.compulynx.excel.dto.TextResponse;
import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.ExcelService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping
    public ResponseEntity<TextResponse> generateExcelFile(@RequestParam Long numberOfRows ) throws IOException {
        TextResponse response = new TextResponse();
        response.setText(this.excelService.generateExcelFile(numberOfRows));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read")
    public ResponseEntity<Map<Integer, List<String>>> readExcelFile(@RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        return ResponseEntity.ok(this.excelService.readExcelFile(multipartFile));
    }

    @PostMapping("/convertToCsv")
    public ResponseEntity<TextResponse> convertExcelToCsv(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        TextResponse response = new TextResponse();
        response.setText(this.excelService.convertExcelFileToCsv(multipartFile));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exportToExcelFile")
    public ResponseEntity<byte[]> exportToExcelFile(@RequestBody List<Student> data) throws IOException {

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.xlsx");

        return new ResponseEntity<>(this.excelService.exportExcelFile(data), headers, HttpStatus.OK);
     }
}
