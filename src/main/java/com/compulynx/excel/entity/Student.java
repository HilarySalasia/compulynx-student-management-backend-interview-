package com.compulynx.excel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    Long studentId;
    String firstName;
    String lastName;
    LocalDate dob;
    String className;
    Integer score;

}
