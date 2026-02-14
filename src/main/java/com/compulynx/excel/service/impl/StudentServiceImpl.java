package com.compulynx.excel.service.impl;

import com.compulynx.excel.entity.Student;
import com.compulynx.excel.repository.StudentRepository;
import com.compulynx.excel.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    /**
     * This method takes raw data as a list of lists of strings, where each inner list represents a row of data.
     * It skips the first row (assumed to be headers), maps each subsequent row to a Student entity, and saves all
     * students to the database. The score is adjusted by adding 10 to the original value.
     *
     * @param rawData A list of lists of strings representing the raw data to be saved.
     * @return A list of Student entities that were saved to the database.
     */
    public List<Student> saveStudentData(List<List<String>> rawData) {

        List<Student> students = rawData.stream()
            .skip(1)
            .map(row -> {
                Student student = new Student();

                student.setStudentId(Double.valueOf(row.get(0)).longValue());
                student.setFirstName(row.get(1));
                student.setLastName(row.get(2));
                student.setDob(LocalDate.parse(row.get(3)));
                student.setClassName(row.get(4));
                student.setScore(Double.valueOf(row.get(5)).intValue() + 10);

                return student;
            })
            .collect(Collectors.toList());

        return studentRepository.saveAll(students);
    }

    /** This method retrieves all students from the database and returns them as a list.
     *
     * @return A list of all Student entities in the database.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /** This method deletes all student records from the database. */
    public void deleteAllStudents() {
        studentRepository.deleteAll();
    }

    /** This method deletes a student record from the database based on the provided ID.
     *
     * @param id The ID of the student to be deleted.
     */
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    /** This method updates an existing student record in the database. It first retrieves the student by ID,
     * then updates the student's details with the provided information, and finally saves the updated student back to the database.
     *
     * @param id             The ID of the student to be updated.
     * @param updatedStudent A Student object containing the updated information for the student.
     * @return The updated Student entity after being saved to the database.
     */
    public Student editStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setFirstName(updatedStudent.getFirstName());
                    student.setLastName(updatedStudent.getLastName());
                    student.setDob(updatedStudent.getDob());
                    student.setClassName(updatedStudent.getClassName());
                    student.setScore(updatedStudent.getScore());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    /** This method retrieves a paginated and sorted list of students from the database. It allows filtering by class name if provided.
     *
     * @param page          The page number to retrieve (0-based index).
     * @param size          The number of records per page.
     * @param sortBy        The field by which to sort the results.
     * @param sortDirection The direction of sorting (ASC or DESC).
     * @param className     An optional parameter to filter students by their class name.
     * @return A Page object containing the requested page of Student entities, sorted and optionally filtered by class name.
     */
    public Page<Student> getStudentsWithPaginationAndSorting(int page, int size, String sortBy,
                                                             String sortDirection, String className) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (className != null && !className.isEmpty()) {
            return studentRepository.findByClassName(className, pageable);
        }

        return studentRepository.findAll(pageable);
    }

    /** This method retrieves a student from the database based on the provided ID.
     *
     * @param id The ID of the student to be retrieved.
     * @return The Student entity with the specified ID, or throws an exception if not found.
     */
    public Student getStudentById(Long id) {
        return  studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    /** This method retrieves a paginated list of students from the database based on the provided class name.
     *
     * @param className The class name to filter students by.
     * @param pageable  A Pageable object containing pagination information (page number, page size, sorting).
     * @return A Page object containing the requested page of Student entities that match the specified class name.
     */
    public Page<Student> getStudentsByClassName(String className, Pageable pageable) {
        return studentRepository.findByClassName(className, pageable);
    }
}
