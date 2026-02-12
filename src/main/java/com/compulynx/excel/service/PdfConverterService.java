package com.compulynx.excel.service;

import java.io.ByteArrayOutputStream;

public interface PdfConverterService {

    ByteArrayOutputStream convert(String html);
}
