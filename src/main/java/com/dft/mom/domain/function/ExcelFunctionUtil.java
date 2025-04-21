package com.dft.mom.domain.function;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class ExcelFunctionUtil {

    public static Workbook loadWorkbook(String excelFilePath) throws IOException {
        Resource resource = new ClassPathResource(excelFilePath);
        try (InputStream is = resource.getInputStream()) {
            return new XSSFWorkbook(is);
        }
    }
}
