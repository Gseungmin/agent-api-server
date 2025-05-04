package com.dft.mom.domain.excel;

import com.dft.mom.domain.dto.excel.ExcelNutritionDto;
import com.dft.mom.domain.dto.excel.ExcelSubItemDto;
import com.dft.mom.web.exception.excel.ExcelException;
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

    private static final String[] GUIDE_HEADERS = {
            "itemId", "title", "summary", "tag", "category",
            "sub_item_1_id", "sub_item_title1", "sub_item_content1",
            "sub_item_2_id", "sub_item_title2", "sub_item_content2",
            "sub_item_3_id", "sub_item_title3", "sub_item_content3",
            "qna_item_1_id", "qna_item_question1", "qna_item_answer1",
            "qna_item_2_id", "qna_item_question2", "qna_item_answer2"
    };

    private static final String[] POST_HEADERS = {
            "itemId", "title", "summary", "type", "start_period",
            "end_period", "category", "caution",
            "sub_item_1_id", "sub_item_title1", "sub_item_content1",
            "sub_item_2_id", "sub_item_title2", "sub_item_content2",
            "qna_item_1_id", "qna_item_question1", "qna_item_answer1",
            "qna_item_2_id", "qna_item_question2", "qna_item_answer2"
    };

    @PostConstruct
    public void init() throws IOException {
//         exportJsonToExcel("research_post_2_3.json", "excel/createFetalPost.xlsx");
//         exportJsonToExcel("baby_post_0_0.json", "excel/createBabyPost.xlsx");
//         exportNutritionJsonToExcel("baby_food.json", "excel/createNutrition.xlsx");
    }

    /*
     * JSON 데이터를 엑셀화 시켜주는 메서드
     * 1. JSON 데이터 읽어오기
     * 2. 엑셀 존재하는지 확인 - 아직 파일 없으면 예외
     * 3. 시트 존재하는지 확인 - 이미 있으면 예외
     * 4. 워크북 생성 후 저장
     */
    public void exportJsonToExcel(
            String jsonFileName,
            String excelFilePath
    ) throws IOException {
        List<ExcelPostDto> dataList = readJsonFile(jsonFileName);
        Workbook workbook = loadWorkbook(excelFilePath);

        String sheetName = createSheetName(jsonFileName);
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            createHeader(sheet, sheetName);
        }

        appendData(sheet, dataList);
        saveWorkbook(workbook, excelFilePath);
    }

    public void exportNutritionJsonToExcel(
            String jsonFileName,
            String excelFilePath
    ) throws IOException {
        List<ExcelNutritionDto> dataList = readNutritionFile(jsonFileName);
        Workbook workbook = loadWorkbook(excelFilePath);

        String sheetName = createSheetName(jsonFileName);
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            createHeader(sheet, sheetName);
        }

        appendNutritionData(sheet, dataList);
        saveWorkbook(workbook, excelFilePath);
    }

    /* 기존 엑셀 파일 불러오는 메서드 */
    private Workbook loadWorkbook(String excelFilePath) throws IOException {
        Resource resource = new ClassPathResource(excelFilePath);
        if (!resource.exists()) {
            throw new ExcelException(
                    EXCEL_NOT_FOUND.getCode(),
                    EXCEL_NOT_FOUND.getErrorMessage()
            );
        }

        try (InputStream is = resource.getInputStream()) {
            return WorkbookFactory.create(is);
        }
    }

    /* JSON 파일 읽어오는 메서드 */
    private List<ExcelPostDto> readJsonFile(String jsonFileName) throws IOException {
        Resource resource = new ClassPathResource("json/" + jsonFileName);
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            throw new ExcelException(
                    JSON_NOT_FOUND.getCode(),
                    JSON_NOT_FOUND.getErrorMessage()
            );
        }
    }

    private List<ExcelNutritionDto> readNutritionFile(String jsonFileName) throws IOException {
        Resource resource = new ClassPathResource("json/" + jsonFileName);
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            throw new ExcelException(
                    JSON_NOT_FOUND.getCode(),
                    JSON_NOT_FOUND.getErrorMessage()
            );
        }
    }

    /* 시트 헤더 생성 */
    private void createHeader(Sheet sheet, String sheetName) {
        Row header = sheet.createRow(0);
        String[] headers = (sheetName.equals("영양가이드")
                || sheetName.equals("모유수유가이드")
                || sheetName.equals("이유식가이드")
                || sheetName.equals("분유가이드"))
                ? GUIDE_HEADERS
                : POST_HEADERS;

        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }
    }

    /* 시트에 데이터 추가 (기존 데이터 뒤에 이어쓰기) */
    private void appendData(
            Sheet sheet,
            List<ExcelPostDto> dataList
    ) {
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
            appendSubjectList(row, data.getSubjectList(), 8);
        }
    }

    private void appendNutritionData(
            Sheet sheet,
            List<ExcelNutritionDto> dataList
    ) {
        int startRow = sheet.getLastRowNum() + 1;
        for (ExcelNutritionDto data : dataList) {
            Row row = sheet.createRow(startRow++);
            row.createCell(0).setCellValue("");
            row.createCell(1).setCellValue(data.getTitle());
            row.createCell(2).setCellValue(data.getSummary());
            row.createCell(3).setCellValue(data.getTag());
            row.createCell(4).setCellValue(data.getCategory());
            appendSubjectList(row, data.getSubjectList(), 5);
        }
    }

    private void appendSubjectList(
            Row row,
            List<ExcelSubItemDto> subjects,
            int startCellIndex
    ) {
        if (subjects == null || subjects.isEmpty()) {
            return;
        }

        int cellIndex = startCellIndex;
        for (ExcelSubItemDto subj : subjects) {
            row.createCell(cellIndex++).setCellValue("");
            row.createCell(cellIndex++).setCellValue(subj.getTitle());
            row.createCell(cellIndex++).setCellValue(subj.getContent());
        }
    }

    /* 워크북을 파일에 저장 */
    private void saveWorkbook(
            Workbook workbook,
            String excelFilePath
    ) throws IOException {
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

    /* 시트 이름 생성 */
    private String createSheetName(String jsonFileName) {
        return switch (jsonFileName) {
            case "research_nutrition.json" -> "영양가이드";
            case "baby_breast.json" -> "모유수유가이드";
            case "baby_formular.json" -> "분유가이드";
            case "baby_food.json" -> "이유식가이드";
            case "research_post_2_3.json" -> "임신가이드_2_3달";
            case "research_post_4_7.json" -> "임신가이드_4_7달";
            case "research_post_8_10.json" -> "임신가이드_8_10달";
            case "baby_post_0_0.json" -> "육아가이드_0_0달";
            case "baby_post_1_2.json" -> "육아가이드_1_2달";
            case "baby_post_3_4.json" -> "육아가이드_3_4달";
            case "baby_post_5_6.json" -> "육아가이드_5_6달";
            case "baby_post_7_8.json" -> "육아가이드_7_8달";
            case "baby_post_9_10.json" -> "육아가이드_9_10달";
            case "baby_post_11_12.json" -> "육아가이드_11_12달";
            case "baby_post_13_15.json" -> "육아가이드_13_15달";
            case "baby_post_16_18.json" -> "육아가이드_16_18달";
            case "baby_post_19_24.json" -> "육아가이드_19_24달";
            case "baby_mom.json" -> "육아가이드_산모관리";
            case "baby_caution.json" -> "육아가이드_주의";
            case "baby_attachment.json" -> "육아가이드_애착형성";
            case "baby_sleep.json" -> "육아가이드_수면관리";
            case "research_caution_2_3.json",
                 "research_caution_4_7.json",
                 "research_caution_8_10.json" -> "임신가이드_주의";
            default -> jsonFileName;
        };
    }
}