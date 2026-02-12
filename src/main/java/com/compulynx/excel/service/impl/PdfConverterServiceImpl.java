package com.compulynx.excel.service.impl;

import com.compulynx.excel.service.PdfConverterService;
import com.itextpdf.html2pdf.ConverterProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;

@Configuration
class PdfConverterServiceImpl implements PdfConverterService {
    @Value("${server.port}")
    public String servicePort;

    @Override
    public ByteArrayOutputStream convert(String html) {
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:" + servicePort);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        com.itextpdf.html2pdf.HtmlConverter.convertToPdf(html, outputStream, converterProperties);
        return outputStream;
    }
}