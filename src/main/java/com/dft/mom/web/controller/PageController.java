package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.page.res.PageResponseDto;
import com.dft.mom.domain.service.PageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.dft.mom.domain.validator.MemberValidator.validateAuthentication;
import static com.dft.mom.domain.validator.PostValidator.validateTypeAndPeriod;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {

    private final PageService pageService;

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
}