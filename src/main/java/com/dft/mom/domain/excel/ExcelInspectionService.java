package com.dft.mom.domain.excel;

import com.dft.mom.domain.dto.post.InspectionRowDto;
import com.dft.mom.domain.dto.post.SubItemDto;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.BabyPageItem;
import com.dft.mom.domain.entity.post.Inspection;
import com.dft.mom.domain.entity.post.SubItem;
import com.dft.mom.domain.repository.InspectionRepository;
import com.dft.mom.domain.repository.PageItemRepository;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.web.exception.post.PageException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dft.mom.domain.function.ExcelFunctionUtil.*;
import static com.dft.mom.domain.util.PostConstants.TYPE_INSPECTION;
import static com.dft.mom.domain.validator.PostValidator.validateInspectionRows;
import static com.dft.mom.web.exception.ExceptionType.PAGE_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcelInspectionService {

    private final InspectionRepository inspectionRepository;
    private final PageItemRepository pageItemRepository;
    private final PageService pageService;

    /* MultipartFile 업로드용 메서드 */
    public synchronized void createInspection(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            processWorkbook(workbook);
        }
    }

    /* 테스트 업로드용 메서드 */
    public synchronized void createInspection(String excelFilePath) throws IOException {
        try (Workbook workbook = loadWorkbook(excelFilePath)) {
            processWorkbook(workbook);
        }
    }

    /*워크북 공통 처리 로직*/
    private void processWorkbook(Workbook workbook) {
        List<BabyPage> pageList = pageService.getPageList();
        BabyPage babyPage = pageList.stream()
                .filter(page -> page.getType().equals(TYPE_INSPECTION))
                .findFirst()
                .orElseThrow(() -> new PageException(
                        PAGE_NOT_EXIST.getCode(),
                        PAGE_NOT_EXIST.getErrorMessage()
                ));

        for (Sheet sheet : workbook) {
            List<InspectionRowDto> rows = parseSheet(sheet);
            validateInspectionRows(rows);
            List<Inspection> itemList = updateInspectionList(rows);
            syncPageItems(itemList, babyPage);
        }
    }

    @Transactional(readOnly = true)
    public List<BabyPageItem> getPageItemList(List<Long> idList) {
        return pageItemRepository.findBabyPageItemByInspectionIdList(idList);
    }

    private List<InspectionRowDto> parseSheet(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        Row headerRow = sheet.getRow(0);
        List<InspectionRowDto> itemList = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(
                    0,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );
            Cell cell2 = row.getCell(
                    1,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );
            if (cell1 == null && cell2 == null) {
                break;
            }

            InspectionRowDto dto = new InspectionRowDto();
            dto.setItemId(getLongNumericValue(row.getCell(0)));
            dto.setTitle(getStringValue(row.getCell(1)));
            dto.setSummary(getStringValue(row.getCell(2)));
            dto.setStart(getIntegerNumericValue(row.getCell(3)));
            dto.setEnd(getIntegerNumericValue(row.getCell(4)));
            dto.setSubItemList(parseSubItems(row, headerRow, 5));
            itemList.add(dto);
        }

        return itemList;
    }

    private List<Inspection> updateInspectionList(List<InspectionRowDto> rows) {
        List<Long> itemIds = rows.stream()
                .map(InspectionRowDto::getItemId)
                .distinct()
                .toList();

        List<Inspection> existingItemList = inspectionRepository.findInspectionListByItemIdIn(itemIds);
        Map<Long, Inspection> existingMap = existingItemList.stream()
                .collect(Collectors.toMap(
                        Inspection::getItemId,
                        Function.identity()
                ));

        List<Inspection> itemList = new ArrayList<>();
        for (InspectionRowDto dto : rows) {
            Inspection item = existingMap.get(dto.getItemId());
            if (item != null) {
                item.updateInspection(dto);
            } else {
                item = new Inspection(dto);
            }
            syncSubItems(item, dto.getSubItemList());
            itemList.add(item);
        }

        return inspectionRepository.saveAll(itemList);
    }

    private void syncSubItems(
            Inspection item,
            List<SubItemDto> subList
    ) {
        if (subList == null || subList.isEmpty()) {
            return;
        }

        Map<Long, SubItem> existingSubItem = item.getSubItemList().stream()
                .collect(Collectors.toMap(SubItem::getItemId, Function.identity()));

        List<SubItem> updatedSubList = new ArrayList<>();
        for (SubItemDto dto : subList) {
            Long subId = dto.getSubItemId();

            if (subId != null && existingSubItem.containsKey(subId)) {
                SubItem subItem = existingSubItem.get(subId);
                subItem.updateSubItem(dto);
                updatedSubList.add(subItem);
                continue;
            }

            SubItem subItem = new SubItem(dto, item);
            updatedSubList.add(subItem);
        }
    }

    private void syncPageItems(
            List<Inspection> itemList,
            BabyPage babyPage
    ) {
        List<Long> idList = itemList.stream()
                .map(Inspection::getId)
                .toList();
        List<BabyPageItem> existingItems = getPageItemList(idList);

        Set<Long> existingItemIds = existingItems.stream()
                .map(bpi -> bpi.getInspection().getItemId())
                .collect(Collectors.toSet());

        List<BabyPageItem> addList = new ArrayList<>();
        for (Inspection inspection : itemList) {
            if (!existingItemIds.contains(inspection.getItemId())) {
                addList.add(new BabyPageItem(babyPage, inspection));
            }
        }

        if (!addList.isEmpty()) {
            pageItemRepository.saveAll(addList);
        }
    }
}