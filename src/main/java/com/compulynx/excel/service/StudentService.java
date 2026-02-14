package com.compulynx.excel.service;

import com.compulynx.excel.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

public interface StudentService {

    List<Student> saveStudentData(List<List<String>> rawData);

    List<Student> getAllStudents();

    void deleteAllStudents();

    void deleteStudentById(Long id);

    Student editStudent(Long id, Student updatedStudent);

    Page<Student> getStudentsWithPaginationAndSorting(int page, int size, String sortBy, String sortDirection,
                                                      String className);

    Student getStudentById(Long id);

    Page<Student> getStudentsByClassName(String className, Pageable pageable);
}
