package com.dft.mom.domain.excel;

import com.dft.mom.domain.dto.baby.post.PostRowDto;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.BabyPageItem;
import com.dft.mom.domain.entity.post.Post;
import com.dft.mom.domain.repository.PageItemRepository;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static com.dft.mom.domain.validator.PostValidator.validateRows;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcelPostService {

    private final ObjectMapper objectMapper;
    private final PageRepository pageRepository;
    private final PostRepository postRepository;
    private final PageItemRepository pageItemRepository;

    @PostConstruct
    public void init() throws IOException {
    }

    /*
     * 1. EXCEL 파싱 후 POST 생성 및 업데이트
     * 2. PAGE에 POST가 없다면 새로운 연결 생성, 만약 이미 연결되어있던 POST가 EXCEL에 없다면 연관관계 해제
     * 쿼리 : 1(페이지 조회) + 시트당 { 1(포스트와 연관관계된 페이지 아이템 조회) + 1(포스트 조회) + @(삽입 쿼리) }
     * */
    public synchronized void createPost(String excelFilePath) throws IOException {
        Workbook workbook = loadWorkbook(excelFilePath);
        List<BabyPage> pageList = getPageList();

        for (Sheet sheet : workbook) {
            List<PostRowDto> rows = parseSheet(sheet);
            validateRows(rows);
            List<Post> postList = updatePostList(rows);
            syncPageItems(postList, pageList, rows);
        }
    }

    /*
     * PAGE 전체 조회
     * */
    @Transactional(readOnly = true)
    public List<BabyPage> getPageList() {
        return pageRepository.findAll();
    }

    /*
     * PAGE ITEM 조회
     * */
    @Transactional(readOnly = true)
    public List<BabyPageItem> getPageItemList(List<Long> idList) {
        return pageItemRepository.findBabyPageItemByIdList(idList);
    }

    /*
     * POST 엑셀 시트 파싱
     * */
    private List<PostRowDto> parseSheet(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        List<PostRowDto> itemList = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

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
            dto.setCaution(cautionCell != null && cautionCell.getCellType() == CellType.BOOLEAN && cautionCell.getBooleanCellValue());

            itemList.add(dto);
        }

        return itemList;
    }

    /*
     * POST 데이터 삽입 및 수정 - IN 쿼리 최적화
     * 1. IN 쿼리를 통해 전체 POST 조회
     * 2. 만약 조회되면 업데이트, 조회해서 없을 시 생성
     * */
    private List<Post> updatePostList(List<PostRowDto> rows) {
        List<Long> itemIds = rows.stream().map(PostRowDto::getItemId).distinct().toList();

        List<Post> existingPostList = postRepository.findPostListByItemIdIn(itemIds);

        Map<Long, Post> existingMap = existingPostList.stream()
                .collect(Collectors.toMap(Post::getItemId, Function.identity()));

        List<Post> postList = new ArrayList<>();

        for (PostRowDto dto : rows) {
            Post post = existingMap.get(dto.getItemId());

            if (post != null) {
                post.updatePost(dto);
                postList.add(post);
                continue;
            }

            Post newPost = new Post(dto);
            postList.add(newPost);
        }

        return postRepository.saveAll(postList);
    }

    /*
     * POST 연관관계 생성 및 해제
     * 1. 기존 POST에 연결되어있던 모든 연관관계 조회 - IN 쿼리를 통해 전체 BABY_PAGE_ITEM 조회
     * 2. 각 POST ROW를 돌면서 연관관계 생성
     *   - POST ROW에 해당하는 POST 조회
     *   - POST와 현재 연결된 PAGE의 아이디를 조회 = A
     *   - POST와 연결이 필요한 PAGE의 아이디를 조회 = B
     *   - A-B는 추가 연결이 필요한 데이터
     *   - B-A는 연결 제거가 필요한 데이터
     * */
    private void syncPageItems(List<Post> postList, List<BabyPage> pageList, List<PostRowDto> rows) {
        List<Long> postIds = postList.stream().map(Post::getId).toList();
        List<BabyPageItem> existingItems = getPageItemList(postIds);

        //포스트의 아이디와 포스트 매핑
        Map<Long, Post> postMap = postList.stream()
                .collect(Collectors.toMap(Post::getItemId, Function.identity()));

        //포스트의 아이디와 현재 연결된 페이지 아이템을 매핑
        Map<Long, List<BabyPageItem>> pageItemListMapByPostId = existingItems.stream()
                .collect(Collectors.groupingBy(pageItem -> pageItem.getPost().getId()));

        List<BabyPageItem> addList = new ArrayList<>();
        List<BabyPageItem> removeList = new ArrayList<>();

        for (PostRowDto dto : rows) {
            Post post = postMap.get(dto.getItemId());
            if (post == null) continue;

            //현재 포스트와 연결된 페이지 아이디 셋
            Set<Long> connectedPageIds = pageItemListMapByPostId.getOrDefault(post.getId(), List.of())
                    .stream()
                    .map(pageItem -> pageItem.getBabyPage().getId())
                    .collect(Collectors.toSet());

            //포스트와 연결되어야 할 페이지 아이디 셋
            Set<Long> needPageIds = pageList.stream()
                    .filter(page -> page.getType().equals(dto.getType())
                            && page.getPeriod() >= dto.getStartPeriod()
                            && page.getPeriod() <= dto.getEndPeriod())
                    .map(BabyPage::getId).collect(Collectors.toSet());

            //추가할 페이지 = needPageIds - connectedPageIds = 필요한 연결에서 이미 되어있는 연결 제외
            Set<Long> addIds = new HashSet<>(needPageIds);
            addIds.removeAll(connectedPageIds);

            for (Long pageId : addIds) {
                BabyPage page = pageList.stream()
                        .filter(pg -> pg.getId().equals(pageId))
                        .findFirst()
                        .orElseThrow();

                BabyPageItem newItem = new BabyPageItem(page, post);
                addList.add(newItem);
            }

            //제거할 페이지 = connectedPageIds - needPageIds = 현재 연결된 상태에서 필요한 연결 제외
            Set<Long> removeIds = new HashSet<>(connectedPageIds);
            removeIds.removeAll(needPageIds);

            for (BabyPageItem pageItem : pageItemListMapByPostId.getOrDefault(post.getId(), List.of())) {
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