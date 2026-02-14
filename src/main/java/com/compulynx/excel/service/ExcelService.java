package com.compulynx.excel.service;

import com.compulynx.excel.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExcelService {

    String generateExcelFile(Long noRows) throws IOException, InterruptedException;
    Map<Integer, List<String>> readExcelFile(MultipartFile multipartFile) throws IOException;
    String convertExcelFileToCsv(MultipartFile multipartFile) throws IOException;

    byte[] exportExcelFile() throws IOException;
}
