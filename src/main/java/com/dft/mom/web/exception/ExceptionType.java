package com.dft.mom.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    /**
     * EXCEL Exception
     * */
    EXCEL_NOT_FOUND( 70000, "해당 엑셀파일이 존재하지 않습니다."),
    SHEET_ALREADY_EXIST( 70001, "시트 정보가 이미 존재합니다."),
    JSON_NOT_FOUND( 70002, "생성하려는 JSON파일이 존재하지 않습니다.");

    /**
     * DTO Exception
     * */
    private final int code;
    private final String errorMessage;
}
