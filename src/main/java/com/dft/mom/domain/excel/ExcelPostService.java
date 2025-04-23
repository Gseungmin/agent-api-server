package com.dft.mom.domain.excel;

import com.dft.mom.domain.dto.post.PostRowDto;
import com.dft.mom.domain.dto.post.SubItemDto;
import com.dft.mom.domain.entity.post.*;
import com.dft.mom.domain.repository.PageItemRepository;
import com.dft.mom.domain.repository.PostRepository;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.web.exception.post.PageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dft.mom.domain.function.ExcelFunctionUtil.*;
import static com.dft.mom.domain.validator.PostValidator.validateRows;
import static com.dft.mom.web.exception.ExceptionType.PAGE_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcelPostService {

    private final PostRepository postRepository;
    private final PageItemRepository pageItemRepository;
    private final PageService pageService;

    /* MultipartFile 업로드용 메서드 */
    public synchronized void createPost(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            processPostWorkbook(workbook);
        }
    }

    /* 기존 파일경로 기반 메서드 */
    public synchronized void createPost(String excelFilePath) throws IOException {
        try (Workbook workbook = loadWorkbook(excelFilePath)) {
            processPostWorkbook(workbook);
        }
    }

    /*
     * 1. EXCEL 파싱 후 POST 생성 및 업데이트
     * 2. PAGE에 POST가 없다면 새로운 연결 생성, 만약 이미 연결되어있던 POST가 EXCEL에 없다면 연관관계 해제
     *    쿼리 : 1(페이지 조회) + 시트당 { 1(포스트와 연관관계된 페이지 아이템 조회) + 1(포스트 조회) + @(삽입 쿼리) }
     * 3. 모든 정리 후에 페이지 버전 업데이트 및 캐시 업데이트
     */
    private void processPostWorkbook(Workbook workbook) {
        List<BabyPage> pageList = pageService.getPageList();
        if (pageList.isEmpty()) {
            throw new PageException(
                    PAGE_NOT_EXIST.getCode(),
                    PAGE_NOT_EXIST.getErrorMessage()
            );
        }

        for (Sheet sheet : workbook) {
            List<PostRowDto> rows = parseSheet(sheet);
            validateRows(rows);
            List<Post> postList = updatePostList(rows);
            syncPageItems(postList, pageList, rows);
        }
    }

    /* PAGE ITEM 조회 */
    @Transactional(readOnly = true)
    public List<BabyPageItem> getPageItemList(List<Long> idList) {
        return pageItemRepository.findBabyPageItemByIdList(idList);
    }

    /* POST 엑셀 시트 파싱 */
    private List<PostRowDto> parseSheet(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        Row headerRow = sheet.getRow(0);
        List<PostRowDto> itemList = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell cell2 = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell1 == null && cell2 == null) {
                break;
            }

            PostRowDto dto = new PostRowDto();
            dto.setItemId(getLongNumericValue(row.getCell(0)));
            dto.setTitle(getStringValue(row.getCell(1)));
            dto.setSummary(getStringValue(row.getCell(2)));
            dto.setType(getIntegerNumericValue(row.getCell(3)));
            dto.setStartPeriod(getIntegerNumericValue(row.getCell(4)));
            dto.setEndPeriod(getIntegerNumericValue(row.getCell(5)));
            dto.setCategory(getIntegerNumericValue(row.getCell(6)));

            Cell cautionCell = row.getCell(7);
            boolean caution = cautionCell != null
                    && cautionCell.getCellType() == CellType.BOOLEAN
                    && cautionCell.getBooleanCellValue();
            dto.setCaution(caution);

            dto.setSubItemList(parseSubItems(row, headerRow, 8));
            itemList.add(dto);
        }

        return itemList;
    }

    /*
     * POST 데이터 삽입 및 수정 - IN 쿼리 최적화
     * 1. IN 쿼리를 통해 전체 POST 조회
     * 2. 만약 조회되면 업데이트, 조회해서 없을 시 생성
     */
    private List<Post> updatePostList(List<PostRowDto> rows) {
        List<Long> itemIds = rows.stream()
                .map(PostRowDto::getItemId)
                .distinct()
                .toList();

        List<Post> existingPostList = postRepository.findPostListByItemIdIn(itemIds);
        Map<Long, Post> existingMap = existingPostList.stream()
                .collect(Collectors.toMap(Post::getItemId, Function.identity()));

        List<Post> itemList = new ArrayList<>();
        for (PostRowDto dto : rows) {
            Post item = existingMap.get(dto.getItemId());
            if (item != null) {
                item.updatePost(dto);
            } else {
                item = new Post(dto);
            }
            syncSubItems(item, dto.getSubItemList());
            itemList.add(item);
        }

        return postRepository.saveAll(itemList);
    }

    private void syncSubItems(
            Post item,
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

    /*
     * POST 연관관계 생성 및 해제
     * 1. 기존 POST에 연결되어있던 모든 연관관계 조회
     * 2. 각 POST ROW를 돌면서 연관관계 생성 및 제거
     */
    private void syncPageItems(
            List<Post> postList,
            List<BabyPage> pageList,
            List<PostRowDto> rows
    ) {
        List<Long> postIds = postList.stream()
                .map(Post::getId)
                .toList();
        List<BabyPageItem> existingItems = getPageItemList(postIds);

        Map<Long, Post> postMap = postList.stream()
                .collect(Collectors.toMap(Post::getItemId, Function.identity()));
        Map<Long, List<BabyPageItem>> pageItemListMapByPostId = existingItems.stream()
                .collect(Collectors.groupingBy(
                        pageItem -> pageItem.getPost().getId()
                ));

        List<BabyPageItem> addList = new ArrayList<>();
        List<BabyPageItem> removeList = new ArrayList<>();

        for (PostRowDto dto : rows) {
            Post post = postMap.get(dto.getItemId());
            if (post == null) {
                continue;
            }

            Set<Long> connectedPageIds = pageItemListMapByPostId
                    .getOrDefault(post.getId(), List.of())
                    .stream()
                    .map(item -> item.getBabyPage().getId())
                    .collect(Collectors.toSet());

            Set<Long> needPageIds = pageList.stream()
                    .filter(page -> page.getType().equals(dto.getType())
                            && page.getPeriod() >= dto.getStartPeriod()
                            && page.getPeriod() <= dto.getEndPeriod()
                    )
                    .map(BabyPage::getId)
                    .collect(Collectors.toSet());

            Set<Long> addIds = new HashSet<>(needPageIds);
            addIds.removeAll(connectedPageIds);
            for (Long pageId : addIds) {
                BabyPage page = pageList.stream()
                        .filter(pg -> pg.getId().equals(pageId))
                        .findFirst()
                        .orElseThrow();
                addList.add(new BabyPageItem(page, post));
            }

            Set<Long> removeIds = new HashSet<>(connectedPageIds);
            removeIds.removeAll(needPageIds);
            for (BabyPageItem pageItem :
                    pageItemListMapByPostId.getOrDefault(post.getId(), List.of())) {
                if (removeIds.contains(pageItem.getBabyPage().getId())) {
                    pageItem.initBabyPageItem();
                    removeList.add(pageItem);
                }
            }
        }

        if (!removeList.isEmpty()) {
            pageItemRepository.saveAll(removeList);
        }
        if (!addList.isEmpty()) {
            pageItemRepository.saveAll(addList);
        }
    }
}
