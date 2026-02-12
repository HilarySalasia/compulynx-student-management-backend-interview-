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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
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

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void deleteAllStudents() {
        studentRepository.deleteAll();
    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

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

    public Page<Student> getStudentsWithPaginationAndSorting(int page, int size, String sortBy,
                                                             String sortDirection, String className) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (className != null && !className.isEmpty()) {
            return studentRepository.findByClassName(className, pageable);
        }

        return studentRepository.findAll(pageable);
    }
}
