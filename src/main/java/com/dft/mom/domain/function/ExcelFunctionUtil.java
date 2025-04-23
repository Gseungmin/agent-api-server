package com.dft.mom.domain.function;

import com.dft.mom.domain.dto.post.SubItemDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelFunctionUtil {

    public static Workbook loadWorkbook(String excelFilePath) throws IOException {
        Resource resource = new ClassPathResource(excelFilePath);
        try (InputStream is = resource.getInputStream()) {
            return new XSSFWorkbook(is);
        }
    }

    public static List<SubItemDto> parseSubItems(Row row, Row headerRow, int startIndex) {
        List<SubItemDto> subItems = new ArrayList<>();
        int lastCellIndex = headerRow.getLastCellNum();

        for (int cellIndex = startIndex; cellIndex + 2 < lastCellIndex; cellIndex += 3) {
            Cell idCell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (idCell == null) {
                break;
            }
            Long subItemId = getLongNumericValue(idCell);
            String title = getStringValue(row.getCell(cellIndex + 1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
            String content = getStringValue(row.getCell(cellIndex + 2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));

            Cell headerCell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String headerName = headerCell != null ? headerCell.getStringCellValue() : "";
            boolean isQna = !headerName.startsWith("sub_item");

            subItems.add(new SubItemDto(subItemId, title, content, isQna));
        }
        return subItems;
    }

    /* 숫자 셀 값 추출 */
    public static Long getLongNumericValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /* 숫자 셀 값 추출 */
    public static Integer getIntegerNumericValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /* 문자열 셀 값 추출 */
    public static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }
}