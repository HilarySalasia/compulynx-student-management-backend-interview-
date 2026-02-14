package com.compulynx.excel.service.impl;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.repository.StudentRepository;
import com.compulynx.excel.service.ExcelService;
import com.github.pjfanning.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import net.datafaker.Faker;
import org.slf4j.Logger;
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
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final StudentServiceImpl studentService;

    record StudentData(long id, String first, String last, String dob, String className, int score) {}

    public String  generateExcelFile(Long noRows) throws IOException, InterruptedException {
        int threadCount = Runtime.getRuntime().availableProcessors();

        BlockingQueue<StudentData> queue = new LinkedBlockingQueue<>(10000);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long rowsPerThread = noRows / threadCount;
        for (int i = 0; i < threadCount; i++) {
            long startId = (i * rowsPerThread) + 1;
            long endId = (i == threadCount - 1) ? noRows : startId + rowsPerThread - 1;
            executor.execute(() -> generateData(startId, endId, queue));
        }


        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("Students");
        createHeader(sheet);

        int rowsWritten = 0;
        while (rowsWritten < noRows) {
            StudentData data = queue.poll(5, TimeUnit.SECONDS); // Wait for data
            if (data != null) {
                rowsWritten++;
                writeRow(sheet, rowsWritten, data);
            }
        }

        executor.shutdown();
        return saveFile(workbook, noRows);
    }

    public void generateData(long start, long end, BlockingQueue<StudentData> queue) {
        Faker faker = new Faker();

        java.util.Date from = java.sql.Date.valueOf("2000-01-01");
        java.util.Date to = java.sql.Date.valueOf("2010-12-31");
        for (long i = start; i <= end; i++) {
            try {

                LocalDate randomDate = faker.date().between(from, to)
                        .toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();

                queue.put(new StudentData(
                        i,
                        faker.name().firstName(),
                        faker.name().lastName(),
                        randomDate.toString(),
                        "Class" + (ThreadLocalRandom.current().nextInt(5) + 1),
                        ThreadLocalRandom.current().nextInt(55, 76)
                ));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void createHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] columns = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
    }

    private void writeRow(Sheet sheet, int rowNum, StudentData data) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(data.id());
        row.createCell(1).setCellValue(data.first());
        row.createCell(2).setCellValue(data.last());
        row.createCell(3).setCellValue(data.dob());
        row.createCell(4).setCellValue(data.className());
        row.createCell(5).setCellValue(data.score());
    }

    private String saveFile(SXSSFWorkbook workbook, Long noRows) throws IOException {
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

    /**
     * Reads an Excel file and returns a map where the key is the row number and the value is a list of cell values in that row.
     * Uses Apache POI's StreamingReader for efficient memory usage when reading large files.
     */
    public Map<Integer, List<String>> readExcelFile(MultipartFile multipartFile) throws IOException {
        IOUtils.setByteArrayMaxOverride(600_000_000);
        Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
        InputStream file = multipartFile.getInputStream();
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(10000)
                .bufferSize(4096)
                .open(file);

        Sheet sheet =  workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        Map<Integer, List<String>> data2 = new HashMap<>();
        int i = 0;
        Iterator<Row> rowIterator = sheet.rowIterator();
        log.info("Starting to read rows in batches...");
        List<Row> batch = new ArrayList<>();
        while(rowIterator.hasNext()) {
            batch.add(rowIterator.next());
            if(batch.size() == 10000) {
                for (Row row : batch) {
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
                batch.clear();
            }
        }
        log.info("Excel file read successfully with " + data.size() + " rows.");
        return  data;
    }

    /**
     * Converts the given Excel data (in the form of a map) to CSV format and returns it as a ByteArrayInputStream.
     * The data is sorted by row number before conversion to ensure the correct order in the CSV output.
     */
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

    /**
     * Converts an Excel file (provided as a MultipartFile) to CSV format and saves it to the file system.
     * The method reads the Excel file, converts its content to CSV, and writes the CSV data to a specified location on the disk.
     * It returns a success message with the location of the saved CSV file.
     */
    public String convertExcelFileToCsv(MultipartFile multipartFile) throws IOException {

        Map<Integer, List<String>> excelData = readExcelFile(multipartFile);

        ByteArrayInputStream csvData = convertExcelDatatoCsv(excelData);

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
        String fileLocation = Paths.get(baseDir, "student_data.csv").toString();

        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            byte[] buffer = new byte[csvData.available()];
            csvData.read(buffer);
            outputStream.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing CSV data to file: " + e.getMessage());
        }

        return "Excel file converted to CSV successfully. In the file location: " + fileLocation;

    }

    /**
     * Exports student data from the database to an Excel file and returns it as a byte array.
     * The method creates an Excel workbook, populates it with student data, and then writes the workbook to a byte array output stream.
     * Finally, it returns the byte array representation of the Excel file.
     */
    public byte[] exportExcelFile() throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Students");

        // Create Header Row
        Row header = sheet.createRow(0);
        String[] columns = {"Student ID", "First Name", "Last Name", "Date of Birth", "Class Name", "Score"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        Iterator<Student> studentsIterator = studentService.getAllStudents().iterator();

        int rowNum = 1;
        while (studentsIterator.hasNext()) {
            Student student = studentsIterator.next();
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1); // ID Column
            row.createCell(1).setCellValue(student.getFirstName());
            row.createCell(2).setCellValue(student.getLastName());
            row.createCell(3).setCellValue(student.getDob() != null ? student.getDob().toString() : "");
            row.createCell(4).setCellValue(student.getClassName());
            row.createCell(5).setCellValue(student.getScore());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);

        workbook.dispose();
        workbook.close();

        return bos.toByteArray();
    }



}
