package com.compulynx.excel.service;

import com.compulynx.excel.entity.Student;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

public interface PdfConverterService {

    ByteArrayOutputStream convert(String html);

    void convertLargeDataToPdf(Iterable<Student> students, OutputStream os);
}
