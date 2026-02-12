package com.compulynx.excel.service.impl;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.repository.StudentRepository;
import com.compulynx.excel.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import net.datafaker.Faker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final StudentRepository studentRepository;

    public String  generateExcelFile(Long noRows) throws IOException {
        Faker faker = new Faker();

        // Define your boundaries (Timestamp format: YYYY-MM-DD HH:MM:SS)
        Timestamp from = Timestamp.valueOf("2000-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2010-12-31 23:59:59");

        System.out.println(" From Date: " + from + "To Date: " + to);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        sheet.createRow(0).createCell(0).setCellValue("Student ID");
        sheet.getRow(0).createCell(1).setCellValue("First Name");
        sheet.getRow(0).createCell(2).setCellValue("Last Name");
        sheet.getRow(0).createCell(3).setCellValue("Date of Birth");
        sheet.getRow(0).createCell(4).setCellValue("Class Name");
        sheet.getRow(0).createCell(5).setCellValue("Score");

        for (int i = 1; i <= noRows; i++) {
            sheet.createRow(i).createCell(0).setCellValue(i);
            sheet.getRow(i).createCell(1).setCellValue(faker.name().firstName());
            sheet.getRow(i).createCell(2).setCellValue(faker.name().lastName());
            sheet.getRow(i).createCell(3).setCellValue(faker.date().between(from, to).toLocalDateTime().toLocalDate().toString());
            sheet.getRow(i).createCell(4).setCellValue("Class" + (i % 5 + 1));
            sheet.getRow(i).createCell(5).setCellValue(Math.round(Math.random() * (75 - 55 + 1) + 55));
        }

        String baseDir;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            baseDir = "C:\\var\\log\\applications\\API\\dataprocessing\\";
        } else {
            baseDir = "/var/log/applications/API/dataprocessing/";
        }

        Path basePath = Paths.get(baseDir);

        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }

        File currentDirectory =  new File(baseDir);
        String path = currentDirectory.getAbsolutePath();
        String fileLocation = Paths.get(baseDir, "student_data.xlsx").toString();

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();

        return "Excel file generated with " + noRows + " rows. In the file location: " + fileLocation;
    }


    public Map<Integer, List<String>> readExcelFile(MultipartFile multipartFile) throws IOException {
        InputStream file = multipartFile.getInputStream();
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet =  workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;

        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();

            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING -> rowData.add(cell.getStringCellValue());

                    case NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            rowData.add(cell.getDateCellValue().toString());
                        } else {
                            rowData.add(String.valueOf(cell.getNumericCellValue()));
                        }
                    }

                    case BOOLEAN -> rowData.add(String.valueOf(cell.getBooleanCellValue()));

                    case FORMULA -> rowData.add(cell.getCellFormula());

                    case BLANK -> rowData.add("");

                    default -> rowData.add("");
                }
            }

            data.put(row.getRowNum(), rowData);
        }
        return  data;
    }

    public ByteArrayInputStream convertExcelDatatoCsv(Map<Integer, List<String>> excelData) {
        Map<Integer, List<String>> sortedData = new TreeMap<>(excelData);

        try{
            ByteArrayOutputStream out =  new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter( new PrintWriter(out), CSVFormat.DEFAULT);
            for (Map.Entry<Integer, List<String>> entry : sortedData.entrySet()) {
                csvPrinter.printRecord(entry.getValue());
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error while converting Excel data to CSV: " + e.getMessage());
        }

    }

    public String convertExcelFileToCsv(MultipartFile multipartFile) throws IOException {

        Map<Integer, List<String>> excelData = readExcelFile(multipartFile);

        ByteArrayInputStream csvData = convertExcelDatatoCsv(excelData);


        File currentDirectory =  new File(".");
        String path = currentDirectory.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "student_data.csv";

        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            byte[] buffer = new byte[csvData.available()];
            csvData.read(buffer);
            outputStream.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing CSV data to file: " + e.getMessage());
        }

        return "Excel file converted to CSV successfully. In the file location: " + fileLocation;

    }

    public byte[] exportExcelFile(List<Student> students) throws IOException {

        Long noRows = (long) students.size();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        sheet.createRow(0).createCell(0).setCellValue("Student ID");
        sheet.getRow(0).createCell(1).setCellValue("First Name");
        sheet.getRow(0).createCell(2).setCellValue("Last Name");
        sheet.getRow(0).createCell(3).setCellValue("Date of Birth");
        sheet.getRow(0).createCell(4).setCellValue("Class Name");
        sheet.getRow(0).createCell(5).setCellValue("Score");

        for (int i = 1; i <= noRows; i++) {
            sheet.createRow(i).createCell(0).setCellValue(i);
            sheet.getRow(i).createCell(1).setCellValue(students.get(i-1).getFirstName());
            sheet.getRow(i).createCell(2).setCellValue(students.get(i-1).getLastName());
            sheet.getRow(i).createCell(3).setCellValue(students.get(i-1).getDob().toString());
            sheet.getRow(i).createCell(4).setCellValue(students.get(i-1).getClassName());
            sheet.getRow(i).createCell(5).setCellValue(students.get(i-1).getScore());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] excelData = outputStream.toByteArray();

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students_export.xlsx");
        return excelData;
    }



}
