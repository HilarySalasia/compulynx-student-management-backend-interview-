package com.compulynx.excel.service;

import com.compulynx.excel.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CSVService {

    List<Student> readAndSaveCsvFile(MultipartFile multipartFile) throws IOException;
    byte[] exportCSVFile(List<Student> students);

    byte[] exportPdfFile(List<Student> students);
}
