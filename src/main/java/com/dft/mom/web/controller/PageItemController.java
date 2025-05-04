package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.item.res.ItemResponseDto;
import com.dft.mom.domain.service.SubItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

import static com.dft.mom.domain.validator.MemberValidator.validateAuthentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class PageItemController {

    private final SubItemService itemService;

    /*아이템 조회*/
    @GetMapping
    public ItemResponseDto getItem(
            Authentication authentication,
            HttpServletRequest request,
            @RequestParam(name = "type") Integer type,
            @RequestParam(name = "itemId") Long itemId,
            @RequestParam(name = "version", required = false) Integer version
    ) {
        validateAuthentication(authentication, request);
        System.out.println("START");
        ItemResponseDto cachedItem = itemService.getCachedItem(type, itemId);

        if (cachedItem == null) {
            System.out.println("PRE-END-1");
            return null;
        }

        if (Objects.equals(cachedItem.getVersion(), version)) {
            System.out.println("PRE-END-2");
            return null;
        }

        System.out.println("END");
        return cachedItem;
    }
}