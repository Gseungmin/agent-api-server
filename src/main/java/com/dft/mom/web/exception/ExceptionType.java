package com.dft.mom.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    /**
     * Common Exception
     * */
    UNKNOWN_ERROR( 90000, "알수없는 에러가 발생하였습니다."),
    TOKEN_NOT_EXIST( 90001, "JWT Token이 존재하지 않습니다."),
    TOKEN_INVALID( 90001, "유효하지 않은 JWT Token 입니다."),
    TOKEN_EXPIRED( 90002, "토큰 만료기간이 지났습니다."),
    BLOCKED_IP( 90100, "의심스러운 활동으로 IP가 잠시 차단되었어요! 관리자에게 문의해주세요."),
    NOT_AVAILABLE_NOW( 90200, "현재 많은 사용자로 인해 작성할수없어요! 잠시만 기다려주세요."),

    /**
     * Member Exception
     * */
    MEMBER_NOT_EXIST( 10000, "회원이 존재하지 않아요."),
    LOGIN_FAILED(10001, "로그인에 실패하였습니다."),
    ADMIN_ONLY( 10002, "관리자만 접근가능합니다."),

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
