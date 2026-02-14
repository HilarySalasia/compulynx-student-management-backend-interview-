package com.compulynx.excel.controller;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * This method retrieves all students from the database and returns them in the response.
     * It uses the StudentService to fetch the data and wraps it in a ResponseEntity with an HTTP status of OK.
     *
     * @return A ResponseEntity containing a list of Student objects and an HTTP status of OK.
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    /**
     * This method retrieves a student by their ID from the database and returns it in the response.
     * It uses the StudentService to fetch the data and wraps it in a ResponseEntity with an HTTP status of OK.
     *
     * @param id The ID of the student to be retrieved.
     * @return A ResponseEntity containing the Student object with the specified ID and an HTTP status of OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }

    /** This method retrieves students by their class name from the database and returns them in a paginated format.
     * It uses the StudentService to fetch the data based on the provided class name and pagination parameters,
     * and wraps it in a ResponseEntity with an HTTP status of OK.
     *
     * @param className The name of the class for which students are to be retrieved.
     * @param page The page number for pagination (starting from 0).
     * @param size The number of records per page for pagination.
     * @return A ResponseEntity containing a Page of Student objects that belong to the specified class name and an HTTP status of OK.
     */
    @GetMapping("/byClassName")
    public ResponseEntity<Page<Student>> getStudentsByClassName(@RequestParam String className,
                                                               @RequestParam int page,
                                                               @RequestParam int size) {
        return new ResponseEntity<>(studentService.getStudentsByClassName(className, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    /** This method updates the information of an existing student in the database based on the provided ID and updated student data.
     * It uses the StudentService to perform the update operation and returns the updated list of students in the response,
     * wrapped in a ResponseEntity with an HTTP status of OK.
     *
     * @param id The ID of the student to be updated.
     * @param updatedStudent A Student object containing the updated information for the student.
     * @return A ResponseEntity containing a list of all Student objects after the update operation and an HTTP status of OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<List<Student>> editStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    /** This method retrieves a paginated and sorted list of students from the database, with optional filtering by class name.
     * It uses the StudentService to fetch the data based on the provided pagination, sorting, and filtering parameters,
     * and wraps it in a ResponseEntity with an HTTP status of OK.
     *
     * @param page The page number for pagination (starting from 0).
     * @param size The number of records per page for pagination.
     * @param sortBy The field by which to sort the results (e.g., "firstName", "lastName").
     * @param sortDirection The direction of sorting ("ASC" for ascending or "DESC" for descending).
     * @param className An optional parameter to filter students by their class name.
     * @return A ResponseEntity containing a Page of Student objects that match the specified criteria and an HTTP status of OK.
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<Student>> getStudentsWithPaginationAndSorting(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) String className) {
        return new ResponseEntity<>(studentService.getStudentsWithPaginationAndSorting(page, size, sortBy,
                sortDirection, className), HttpStatus.OK);
    }

    /** This method deletes a student from the database based on the provided ID.
     * It uses the StudentService to perform the delete operation and returns an HTTP status of NO_CONTENT in the response.
     *
     * @param id The ID of the student to be deleted.
     * @return A ResponseEntity with an HTTP status of NO_CONTENT indicating that the student was successfully deleted.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /** This method deletes all students from the database. It uses the StudentService to perform the
     * delete operation and returns an HTTP status of NO_CONTENT in the response.
     * @return A ResponseEntity with an HTTP status of NO_CONTENT indicating that all students were successfully deleted.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllStudents() {
        studentService.deleteAllStudents();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
