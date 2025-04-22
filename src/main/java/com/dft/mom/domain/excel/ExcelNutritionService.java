package com.dft.mom.domain.excel;

import com.dft.mom.domain.dto.post.NutritionRowDto;
import com.dft.mom.domain.dto.post.PostRowDto;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.BabyPageItem;
import com.dft.mom.domain.entity.post.Nutrition;
import com.dft.mom.domain.entity.post.Post;
import com.dft.mom.domain.repository.NutritionRepository;
import com.dft.mom.domain.repository.PageItemRepository;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.web.exception.post.PageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dft.mom.domain.function.ExcelFunctionUtil.*;
import static com.dft.mom.domain.util.PostConstants.TYPE_PREGNANCY_NUTRITION;
import static com.dft.mom.domain.validator.PostValidator.validateNutritionRows;
import static com.dft.mom.web.exception.ExceptionType.PAGE_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcelNutritionService {

    private final NutritionRepository nutritionRepository;
    private final PageItemRepository pageItemRepository;
    private final PageService pageService;

    @PostConstruct
    public void init() throws IOException {
    }

    public synchronized void createNutrition(String excelFilePath, Integer type) throws IOException {
        Workbook workbook = loadWorkbook(excelFilePath);
        List<BabyPage> pageList = pageService.getPageList();

        BabyPage babyPage = pageList.stream()
                .filter(page -> page.getType().equals(type))
                .findFirst()
                .orElse(null);

        if (babyPage == null) {
            throw new PageException(PAGE_NOT_EXIST.getCode(), PAGE_NOT_EXIST.getErrorMessage());
        }

        for (Sheet sheet : workbook) {
            List<NutritionRowDto> rows = parseSheet(sheet);
            validateNutritionRows(rows);
            List<Nutrition> itemList = updateNutritionList(rows);
            syncPageItems(itemList, babyPage);
        }
    }

    @Transactional(readOnly = true)
    public List<BabyPageItem> getPageItemList(List<Long> idList) {
        return pageItemRepository.findBabyPageItemByNutritionIdList(idList);
    }

    private List<NutritionRowDto> parseSheet(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        List<NutritionRowDto> itemList = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            Cell cell1 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell cell2 = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell1 == null && cell2 == null) {
                break;
            }

            NutritionRowDto dto = new NutritionRowDto();

            dto.setItemId(getLongNumericValue(row.getCell(0)));
            dto.setTitle(getStringValue(row.getCell(1)));
            dto.setSummary(getStringValue(row.getCell(2)));
            dto.setTag(getIntegerNumericValue(row.getCell(3)));
            dto.setCategory(getIntegerNumericValue(row.getCell(4)));

            itemList.add(dto);
        }

        return itemList;
    }

    private List<Nutrition> updateNutritionList(List<NutritionRowDto> rows) {
        List<Long> itemIds = rows.stream().map(NutritionRowDto::getItemId).distinct().toList();

        List<Nutrition> existingNutritionList = nutritionRepository.findNutritionListByItemIdIn(itemIds);

        Map<Long, Nutrition> existingMap = existingNutritionList.stream()
                .collect(Collectors.toMap(Nutrition::getItemId, Function.identity()));

        List<Nutrition> itemList = new ArrayList<>();

        for (NutritionRowDto dto : rows) {
            Nutrition item = existingMap.get(dto.getItemId());

            if (item != null) {
                item.updateNutrition(dto);
                itemList.add(item);
                continue;
            }

            Nutrition newItem = new Nutrition(dto);
            itemList.add(newItem);
        }

        return nutritionRepository.saveAll(itemList);
    }

    private void syncPageItems(List<Nutrition> itemList, BabyPage babyPage) {
        List<Long> IdList = itemList.stream().map(Nutrition::getId).toList();
        List<BabyPageItem> existingItems = getPageItemList(IdList);

        Set<Long> existingItemIds = existingItems.stream()
                .map(babyPageItem -> babyPageItem.getNutrition().getItemId())
                .collect(Collectors.toSet());

        List<BabyPageItem> addList = new ArrayList<>();
        for (Nutrition nutrition : itemList) {
            if (!existingItemIds.contains(nutrition.getItemId())) {
                BabyPageItem bpi = new BabyPageItem(babyPage, nutrition);
                addList.add(bpi);
            }
        }

        if (!addList.isEmpty()) {
            pageItemRepository.saveAll(addList);
        }
    }
}