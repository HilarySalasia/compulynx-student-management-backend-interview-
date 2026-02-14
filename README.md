# Student Management System - Excel Processing API

## Overview

The Student Management System is a Spring Boot REST API application designed for processing Excel files, managing student data, and exporting data in multiple formats (Excel, CSV, PDF). The application provides comprehensive functionality for bulk data operations, file format conversions, and efficient handling of large datasets.

### For Product Managers

This application serves as a backend API for student data management with the following key capabilities:

- **Excel File Processing**: Generate, read, and convert Excel files containing student information
- **Data Import**: Upload CSV files to bulk import student records into the database
- **Data Export**: Export student data in Excel, CSV, or PDF formats
- **Student Management**: Full CRUD operations with advanced features like pagination, sorting, and filtering
- **Performance Optimized**: Handles large datasets efficiently using streaming and multi-threading

The API is designed to integrate with frontend applications (configured for Angular on `localhost:4200`) and provides RESTful endpoints for all operations.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Directory Setup for File Storage](#directory-setup-for-file-storage)
- [Database Configuration](#database-configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Architecture](#architecture)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

---

## Technology Stack

### Core Framework
- **Spring Boot 3.4.5** - Application framework
- **Java 17** - Programming language
- **Gradle** - Build tool

### Key Dependencies
- **Spring Data JPA** - Database persistence
- **PostgreSQL** - Relational database
- **Apache POI 5.4.1** - Excel file processing
- **iText HTML2PDF 6.1.0** - PDF generation
- **Thymeleaf** - HTML templating for PDF reports
- **SpringDoc OpenAPI 2.4.0** - API documentation
- **DataFaker 2.5.3** - Test data generation
- **Lombok** - Code generation

---

## Features

### Excel Operations
- ✅ Generate Excel files with configurable number of rows (multi-threaded)
- ✅ Read and parse Excel files (streaming for large files)
- ✅ Convert Excel files to CSV format
- ✅ Export student data to Excel format

### CSV Operations
- ✅ Upload CSV files and import student data
- ✅ Export student data to CSV format

### PDF Operations
- ✅ Export student data to PDF format (streaming for large datasets)
- ✅ HTML template-based PDF generation

### Student Management
- ✅ Create, Read, Update, Delete operations
- ✅ Pagination and sorting support
- ✅ Filter by class name
- ✅ Bulk operations (delete all students)

---

## Prerequisites

Before installing, ensure you have:

1. **Java 17** or higher installed
   ```bash
   java -version
   ```

2. **PostgreSQL** database server running
   ```bash
   psql --version
   ```

3. **Gradle** (or use the included Gradle Wrapper)
   ```bash
   ./gradlew --version
   ```

4. **Network access** to Maven Central (for dependency downloads)

---

## Installation & Setup

### 1. Clone or Navigate to the Project

```bash
cd /home/majesty/Desktop/Complynx2/Excel
```

### 2. Build the Project

```bash
./gradlew clean build
```

### 3. Configure Database

Update `src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/excelpractical
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 4. Create Database

Connect to PostgreSQL and create the database:

```sql
CREATE DATABASE excelpractical;
```

The application will automatically create tables on first run (via `spring.jpa.hibernate.ddl-auto=update`).

---

## Directory Setup for File Storage

The application saves generated files (Excel and CSV) to specific directories based on the operating system. **These directories must be created or the application will attempt to create them automatically.**

### Required Directories

#### For Linux/Unix/macOS:
```bash
/var/log/applications/API/dataprocessing/
```

#### For Windows:
```
C:\var\log\applications\API\dataprocessing\
```

### Manual Directory Creation

#### Linux/Unix/macOS:
```bash
# Create the directory structure
sudo mkdir -p /var/log/applications/API/dataprocessing

# Set appropriate permissions (adjust user/group as needed)
sudo chown -R $USER:$USER /var/log/applications/API/dataprocessing
sudo chmod -R 755 /var/log/applications/API/dataprocessing
```

**Note**: If you don't have sudo access, you can modify the code to use a directory in your home folder or a location where you have write permissions.

#### Windows:
```powershell
# Open PowerShell as Administrator and run:
New-Item -ItemType Directory -Force -Path "C:\var\log\applications\API\dataprocessing"
```

Or using Command Prompt (as Administrator):
```cmd
mkdir C:\var\log\applications\API\dataprocessing
```

### Alternative: Using User Directory

If you prefer to use a directory in your home folder or a location with write permissions, you can modify the base directory in the code:

**Files to modify:**
- `src/main/java/com/compulynx/excel/service/impl/ExcelServiceImpl.java` (lines 120-128 and 236-243)

**Example change for Linux:**
```java
// Change from:
baseDir = "/var/log/applications/API/dataprocessing/";

// To (using home directory):
baseDir = System.getProperty("user.home") + "/excel_files/";
```

### Verify Directory Permissions

After creating directories, verify write permissions:

**Linux/Unix:**
```bash
touch /var/log/applications/API/dataprocessing/test.txt
rm /var/log/applications/API/dataprocessing/test.txt
```

**Windows:**
```cmd
echo test > C:\var\log\applications\API\dataprocessing\test.txt
del C:\var\log\applications\API\dataprocessing\test.txt
```

---

## Database Configuration

### PostgreSQL Setup

1. **Install PostgreSQL** (if not already installed)

2. **Start PostgreSQL service:**
   ```bash
   # Linux
   sudo systemctl start postgresql
   
   # macOS (Homebrew)
   brew services start postgresql
   
   # Windows
   # Start via Services or pgAdmin
   ```

3. **Create database:**
   ```sql
   CREATE DATABASE excelpractical;
   ```

4. **Update application.properties:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/excelpractical
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

### Database Schema

The application uses JPA/Hibernate with auto-update mode. The `Student` entity creates a table with the following structure:

- **Table Name**: `students`
- **Columns**:
  - `student_id` (Long, Primary Key)
  - `first_name` (String)
  - `last_name` (String)
  - `dob` (LocalDate)
  - `class_name` (String)
  - `score` (Integer)

---

## Running the Application

### Development Mode

```bash
./gradlew bootRun
```

### Production Mode

```bash
./gradlew build
java -jar build/libs/Excel-0.0.1-SNAPSHOT.jar
```

### Verify Application is Running

The application starts on port **8080** by default. Verify it's running:

```bash
curl http://localhost:8080/api/student
```

Or open in browser: `http://localhost:8080`

### API Documentation

Once running, access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

---

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Excel Endpoints

#### 1. Generate Excel File
**GET** `/api/excel?numberOfRows={rows}`

Generates an Excel file with fake student data.

**Parameters:**
- `numberOfRows` (Long, required) - Number of rows to generate

**Response:**
```json
{
  "text": "Excel file generated with 1000 rows. In the file location: /var/log/applications/API/dataprocessing/student_data.xlsx"
}
```

**Example:**
```bash
curl "http://localhost:8080/api/excel?numberOfRows=1000"
```

#### 2. Read Excel File
**GET** `/api/excel/read?file={file}`

Reads and parses an uploaded Excel file.

**Parameters:**
- `file` (MultipartFile, required) - Excel file to read

**Response:**
```json
{
  "0": ["studentId", "firstName", "lastName", "DOB", "class", "score"],
  "1": ["1", "John", "Doe", "2005-03-15", "Class1", "65"],
  ...
}
```

**Example:**
```bash
curl -X GET "http://localhost:8080/api/excel/read?file=@students.xlsx"
```

#### 3. Convert Excel to CSV
**POST** `/api/excel/convertToCsv?file={file}`

Converts an Excel file to CSV format and saves it.

**Parameters:**
- `file` (MultipartFile, required) - Excel file to convert

**Response:**
```json
{
  "text": "Excel file converted to CSV successfully. In the file location: /var/log/applications/API/dataprocessing/student_data.csv"
}
```

**Example:**
```bash
curl -X POST "http://localhost:8080/api/excel/convertToCsv?file=@students.xlsx"
```

#### 4. Export to Excel File
**POST** `/api/excel/exportToExcelFile`

Exports all student data from database to Excel file (downloadable).

**Response:** Binary Excel file (`students_export.xlsx`)

**Example:**
```bash
curl -X POST "http://localhost:8080/api/excel/exportToExcelFile" --output students_export.xlsx
```

---

### CSV Endpoints

#### 1. Upload Students Data
**POST** `/api/csv/uploadStudentsData?file={file}`

Uploads a CSV file and imports student data into the database.

**CSV Format:**
```csv
studentId,firstName,lastName,dob,className,score
1,John,Doe,2005-03-15,Class1,65
2,Jane,Smith,2005-07-22,Class2,70
```

**Parameters:**
- `file` (MultipartFile, required) - CSV file to upload

**Response:** Array of saved Student objects

**Example:**
```bash
curl -X POST "http://localhost:8080/api/csv/uploadStudentsData?file=@students.csv"
```

#### 2. Export to CSV File
**POST** `/api/csv/exportToCsvFile`

Exports all student data to CSV format (downloadable).

**Response:** Binary CSV file (`students_export.pdf` - note: filename is incorrect in code, should be CSV)

**Example:**
```bash
curl -X POST "http://localhost:8080/api/csv/exportToCsvFile" --output students_export.csv
```

#### 3. Export to PDF File
**POST** `/api/csv/exportToPdfFile`

Exports all student data to PDF format (downloadable, streaming for large datasets).

**Response:** Binary PDF file (`students.pdf`)

**Example:**
```bash
curl -X POST "http://localhost:8080/api/csv/exportToPdfFile" --output students.pdf
```

---

### Student Endpoints

#### 1. Get All Students
**GET** `/api/student`

Retrieves all students from the database.

**Response:** Array of Student objects

**Example:**
```bash
curl http://localhost:8080/api/student
```

#### 2. Get Student by ID
**GET** `/api/student/{id}`

Retrieves a specific student by ID.

**Parameters:**
- `id` (Long, path variable) - Student ID

**Example:**
```bash
curl http://localhost:8080/api/student/1
```

#### 3. Get Students by Class Name
**GET** `/api/student/byClassName?className={name}&page={page}&size={size}`

Retrieves students filtered by class name with pagination.

**Parameters:**
- `className` (String, required) - Class name to filter
- `page` (int, required) - Page number (0-based)
- `size` (int, required) - Page size

**Example:**
```bash
curl "http://localhost:8080/api/student/byClassName?className=Class1&page=0&size=10"
```

#### 4. Get Students with Pagination and Sorting
**GET** `/api/student/paginated?page={page}&size={size}&sortBy={field}&sortDirection={direction}&className={name}`

Retrieves students with pagination, sorting, and optional class filtering.

**Parameters:**
- `page` (int, required) - Page number (0-based)
- `size` (int, required) - Page size
- `sortBy` (String, required) - Field to sort by (e.g., "firstName", "lastName", "score")
- `sortDirection` (String, required) - "ASC" or "DESC"
- `className` (String, optional) - Filter by class name

**Example:**
```bash
curl "http://localhost:8080/api/student/paginated?page=0&size=20&sortBy=firstName&sortDirection=ASC&className=Class1"
```

#### 5. Update Student
**PUT** `/api/student/{id}`

Updates a student's information.

**Parameters:**
- `id` (Long, path variable) - Student ID
- Request Body: Student object (JSON)

**Example:**
```bash
curl -X PUT "http://localhost:8080/api/student/1" \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"firstName":"John","lastName":"Doe","dob":"2005-03-15","className":"Class1","score":75}'
```

#### 6. Delete Student
**DELETE** `/api/student/{id}`

Deletes a specific student.

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/student/1"
```

#### 7. Delete All Students
**DELETE** `/api/student`

Deletes all students from the database.

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/student"
```

---

## Architecture

### Project Structure

```
src/
├── main/
│   ├── java/com/compulynx/excel/
│   │   ├── config/
│   │   │   └── CorsConfig.java          # CORS configuration
│   │   ├── controller/
│   │   │   ├── CSVController.java       # CSV endpoints
│   │   │   ├── ExcelController.java     # Excel endpoints
│   │   │   └── StudentController.java   # Student CRUD endpoints
│   │   ├── dto/
│   │   │   └── TextResponse.java        # Response DTO
│   │   ├── entity/
│   │   │   └── Student.java             # Student entity
│   │   ├── repository/
│   │   │   └── StudentRepository.java   # JPA repository
│   │   ├── service/
│   │   │   ├── CSVService.java          # CSV service interface
│   │   │   ├── ExcelService.java        # Excel service interface
│   │   │   ├── PdfConverterService.java # PDF service interface
│   │   │   ├── StudentService.java      # Student service interface
│   │   │   └── impl/
│   │   │       ├── CSVServiceImpl.java
│   │   │       ├── ExcelServiceImpl.java
│   │   │       ├── PdfConverterServiceImpl.java
│   │   │       └── StudentServiceImpl.java
│   │   └── ExcelApplication.java        # Main application class
│   └── resources/
│       ├── application.properties        # Configuration
│       └── templates/
│           ├── students.html            # PDF template
│           └── students.css             # PDF styles
└── test/
    └── java/...                         # Test classes
```

### Key Design Patterns

1. **Service Layer Pattern**: Business logic separated from controllers
2. **Repository Pattern**: Data access abstraction via Spring Data JPA
3. **DTO Pattern**: Data transfer objects for API responses
4. **Streaming Pattern**: Large file processing using streaming readers/writers

### Performance Optimizations

1. **Multi-threading**: Excel generation uses thread pool based on CPU cores
2. **Streaming**: Large Excel files read using `StreamingReader` to avoid memory issues
3. **SXSSFWorkbook**: Streaming Excel writer for large file generation
4. **Iterator-based Processing**: Database queries use iterators to avoid loading all data into memory
5. **Batch Processing**: Excel reading processes rows in batches of 10,000

---

## Configuration

### Application Properties

Key configuration in `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=Excel
server.port=8080

# Database
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:postgresql://localhost:5432/excelpractical
spring.datasource.username=postgres
spring.datasource.password=

# File Upload Limits
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

### CORS Configuration

The application is configured to accept requests from `http://localhost:4200` (Angular frontend). To modify, edit `CorsConfig.java`.

---

## Troubleshooting

### Common Issues

#### 1. Directory Permission Errors

**Error:** `java.io.IOException: Permission denied`

**Solution:**
- Ensure the file storage directory exists and has write permissions
- On Linux, use `sudo` to create directories or change ownership
- Consider using a directory in your home folder if you don't have admin access

#### 2. Database Connection Errors

**Error:** `org.postgresql.util.PSQLException: Connection refused`

**Solution:**
- Verify PostgreSQL is running: `sudo systemctl status postgresql`
- Check database credentials in `application.properties`
- Ensure database `excelpractical` exists
- Verify PostgreSQL is listening on port 5432

#### 3. Port Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**
- Change port in `application.properties`: `server.port=8081`
- Or stop the process using port 8080

#### 4. Out of Memory Errors

**Error:** `java.lang.OutOfMemoryError`

**Solution:**
- Increase JVM heap size: `java -Xmx2g -jar Excel-0.0.1-SNAPSHOT.jar`
- For large files, ensure streaming is used (already implemented)

#### 5. File Upload Size Limits

**Error:** `MaxUploadSizeExceededException`

**Solution:**
- Increase limits in `application.properties`:
  ```properties
  spring.servlet.multipart.max-file-size=200MB
  spring.servlet.multipart.max-request-size=200MB
  ```

### Logging

The application uses SLF4J for logging. To enable debug logging, add to `application.properties`:

```properties
logging.level.com.compulynx.excel=DEBUG
logging.level.org.springframework.web=DEBUG
```

---

## Development Notes

### Code Quality

- Uses Lombok to reduce boilerplate code
- Follows Spring Boot best practices
- Implements proper exception handling
- Uses Java 17 features (records, pattern matching)

### Testing

Run tests with:
```bash
./gradlew test
```

### Building for Production

```bash
./gradlew clean build -x test
```

The JAR file will be in `build/libs/Excel-0.0.1-SNAPSHOT.jar`

---

## License

This project is part of the Compulynx Student Management System.

---

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify all prerequisites are met
4. Ensure directory permissions are correct

---

**Last Updated:** 2024
