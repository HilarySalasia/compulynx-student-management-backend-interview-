package com.compulynx.excel.service.impl;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.CSVService;
import com.compulynx.excel.service.PdfConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSVServiceImpl implements CSVService {

    private final StudentServiceImpl studentService;
    private final TemplateEngine templateEngine;
    private final PdfConverterService converter;
    private static final String COMMA_DELIMITER = ",";
    public List<Student> readAndSaveCsvFile(MultipartFile multipartFile) throws IOException {
        List<List<String>> records;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            records = reader.lines()
                    .map(line -> Arrays.asList(line.split(COMMA_DELIMITER)))
                    .collect(Collectors.toList());
        }

        return studentService.saveStudentData(records);
    }

    public byte[] exportCSVFile(List<Student> students) {
        StringBuilder csvData = new StringBuilder();
        csvData.append("Student ID,First Name,Last Name,Date of Birth,Class Name,Score\n");
        for (Student student : students) {
            csvData.append(student.getStudentId()).append(",")
                    .append(student.getFirstName()).append(",")
                    .append(student.getLastName()).append(",")
                    .append(student.getDob()).append(",")
                    .append(student.getClassName()).append(",")
                    .append(student.getScore()).append("\n");
        }
        return csvData.toString().getBytes();
    }

    public byte[] exportPdfFile(List<Student> students) {
        Context context = new Context();
        context.setVariable("students", students);
        String studentsHtml = templateEngine.process("students.html", context);

        ByteArrayOutputStream byteArrayOutputStream = converter.convert(studentsHtml);

        return byteArrayOutputStream.toByteArray();
    }
}
