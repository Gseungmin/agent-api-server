package com.dft.mom.domain.service;

import com.dft.mom.web.exception.member.ExcelException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dft.mom.domain.dto.excel.ExcelPostDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.dft.mom.web.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
public class ExcelCreateService {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
//        exportJsonToExcel("research_post_2_3.json", "excel/createFetalPost.xlsx");
    }

    /*
     * JSON 데이터를 엑셀화 시켜주는 메서드
     * 1. JSON 데이터 읽어오기
     * 2. 엑셀 존재하는지 확인 - 아직 엑셀파일이 없다면 예외
     * 3. 시트 존재하는지 확인 - 이미 시트가 있다면 예외
     * 4. 워크북 생성 후 저장
     * */
    public void exportJsonToExcel(String jsonFileName, String excelFilePath) throws IOException {
        List<ExcelPostDto> dataList = readJsonFile(jsonFileName);
        Workbook workbook = loadWorkbook(excelFilePath);

        String sheetName = createSheetName(jsonFileName);
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            createHeader(sheet);
        }

        appendData(sheet, dataList);
        saveWorkbook(workbook, excelFilePath);
    }

    /*
     * 기존 엑셀 파일 불러오는 메서드
     * */
    private Workbook loadWorkbook(String excelFilePath) throws IOException {
        Resource resource = new ClassPathResource(excelFilePath);
        if (!resource.exists()) {
            throw new ExcelException(EXCEL_NOT_FOUND.getCode(), EXCEL_NOT_FOUND.getErrorMessage());
        }

        try (InputStream is = resource.getInputStream()) {
            return WorkbookFactory.create(is);
        }
    }

    /*
     * JSON 파일 읽어오는 메서드
     * */
    private List<ExcelPostDto> readJsonFile(String jsonFileName) throws IOException {
        Resource resource = new ClassPathResource("json/" + jsonFileName);
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            throw new ExcelException(JSON_NOT_FOUND.getCode(), JSON_NOT_FOUND.getErrorMessage());
        }
    }

    /*
     * 시트 헤더 생성
     * */
    private void createHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] headers = {"itemId", "title", "summary", "type", "start_period", "end_period", "category", "caution"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }
    }

    /*
     * 시트에 데이터 추가 (기존 데이터 뒤에 이어쓰기)
     */
    private void appendData(Sheet sheet, List<ExcelPostDto> dataList) {
        int startRow = sheet.getLastRowNum() + 1;
        for (ExcelPostDto data : dataList) {
            Row row = sheet.createRow(startRow++);
            row.createCell(0).setCellValue("");
            row.createCell(1).setCellValue(data.getTitle());
            row.createCell(2).setCellValue(data.getSummary());
            row.createCell(3).setCellValue(data.getType());
            row.createCell(4).setCellValue(data.getStart_period());
            row.createCell(5).setCellValue(data.getEnd_period());
            row.createCell(6).setCellValue(data.getCategory());
            row.createCell(7).setCellValue(data.isCaution());
        }
    }

    /*
     * 워크북을 파일에 저장
     */
    private void saveWorkbook(Workbook workbook, String excelFilePath) throws IOException {
        Path path = Paths.get("src", "main", "resources", excelFilePath);
        try (OutputStream os = Files.newOutputStream(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            workbook.write(os);
        } finally {
            workbook.close();
        }
    }

    /*
     * JSON 파일 읽어오는 메서드
     * */
    private String createSheetName(String jsonFileName) {
        return switch (jsonFileName) {
            case "research_post_2_3.json" -> "임신가이드_2_3달";
            case "research_post_4_7.json" -> "임신가이드_4_7달";
            case "research_post_8_10.json" -> "임신가이드_8_10달";
            case "research_caution_2_3.json", "research_caution_4_7.json", "research_caution_8_10.json" -> "임신가이드_주의";
            default -> jsonFileName;
        };
    }
}