package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.page.res.PageResponseDto;
import com.dft.mom.domain.excel.ExcelInspectionService;
import com.dft.mom.domain.excel.ExcelNutritionService;
import com.dft.mom.domain.excel.ExcelPostService;
import com.dft.mom.domain.service.CacheUpdateService;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.domain.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.dft.mom.domain.util.PostConstants.*;
import static com.dft.mom.domain.validator.MemberValidator.validateAuthentication;
import static com.dft.mom.domain.validator.PostValidator.validateTypeAndPeriod;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {

    private final PageService pageService;
    private final ExcelNutritionService nutritionService;
    private final ExcelPostService postService;
    private final ExcelInspectionService inspectionService;
    private final RoleService roleService;
    private final CacheUpdateService cacheUpdateService;

    /*페이지 조회*/
    @GetMapping
    public PageResponseDto getPage(
            Authentication authentication,
            HttpServletRequest request,
            @RequestParam(name = "type") Integer type,
            @RequestParam(name = "period") Integer period,
            @RequestParam(name = "version", required = false) Integer version
    ) {
        validateAuthentication(authentication, request);
        validateTypeAndPeriod(type, period);

        PageResponseDto cachedPage = pageService.getCachedPage(type, period);

        if (cachedPage == null) {
            return null;
        }

        if (Objects.equals(cachedPage.getVersion(), version)) {
            return null;
        }

        return cachedPage;
    }

    /*페이지 생성*/
    @PostMapping
    public void createPage(
            Authentication authentication,
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") Integer type
    ) throws IOException {
        validateAuthentication(authentication, request);
        String role = roleService.getMemberRole(authentication);
        roleService.validateAdmin(role);

        if (type.equals(TYPE_PREGNANCY_GUIDE) || type.equals(TYPE_CHILDCARE_GUIDE)) {
            postService.createPost(file);
            return;
        }

        if (type.equals(TYPE_CHILDCARE_NUTRITION) || type.equals(TYPE_PREGNANCY_NUTRITION)) {
            nutritionService.createNutrition(file, type);
            return;
        }

        inspectionService.createInspection(file);
    }

    /*페이지 업데이트*/
    @PatchMapping
    public void updatePage(
            Authentication authentication,
            HttpServletRequest request,
            @RequestParam("type") Integer type
    ) {
        validateAuthentication(authentication, request);
        String role = roleService.getMemberRole(authentication);
        roleService.validateAdmin(role);

        if (type.equals(TYPE_PREGNANCY_GUIDE) || type.equals(TYPE_CHILDCARE_GUIDE)) {
            cacheUpdateService.updateCachedPost();
        }

        if (type.equals(TYPE_CHILDCARE_NUTRITION) || type.equals(TYPE_PREGNANCY_NUTRITION)) {
            cacheUpdateService.updateCachedNutrition();
        }

        if (type.equals(TYPE_INSPECTION)) {
            cacheUpdateService.updateCachedInspection();
        }

        cacheUpdateService.updateCachedPage();
    }
}