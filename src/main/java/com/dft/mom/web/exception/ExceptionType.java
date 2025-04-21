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
    NOT_AVAILABLE_NOW( 90101, "현재 많은 사용자로 인해 작성할수없어요! 잠시만 기다려주세요."),
    ENCRYPT_FAIL(90102, "암호화에 실패하였습니다."),
    DECRYPT_FAIL(90103, "복호화에 실패하였습니다."),
    TIME_INVALID(90104, "과거로 돌아갈 수 없습니다."),

    /**
     * Member Exception
     * */
    MEMBER_NOT_EXIST( 10000, "회원이 존재하지 않아요."),
    LOGIN_FAILED(10001, "로그인에 실패하였습니다."),
    ADMIN_ONLY( 10002, "관리자만 접근가능합니다."),
    MULTI_LOGIN(10003, "중복 로그인되었습니다."),
    SOCIAL_CONNECT_FAILED(10004, "소셜 로그인은 진행할 수 없어요!"),
    UN_AUTH_NON_MEMBER(10005, "로그인 먼저 해주세요"),

    /**
     * FAMILY Exception
     * */
    FAMILY_NOT_EXIST( 20000, "가족 정보가 존재하지 않아요. 관리자에게 문의해주세요"),
    BABY_NOT_EXIST(20001, "아이 정보가 존재하지 않아요. 관리자에게 문의해주세요"),
    UN_AUTH_BABY(20002, "아이에 접근 권한이 존재하지 않아요. 관리자에게 문의해주세요"),
    FAMILY_CODE_INVALID(20003, "코드 정보가 잘못되었어요"),
    ALREADY_CONNECTED_FAMILY(20004, "이미 연결되었어요"),
    FAIL_CREATE_FAMILY_CODE( 20005, "가족 코드 생성에 실패하였습니다."),

    /**
     * EXCEL Exception
     * */
    EXCEL_NOT_FOUND( 70000, "해당 엑셀파일이 존재하지 않습니다."),
    SHEET_ALREADY_EXIST( 70001, "시트 정보가 이미 존재합니다."),
    JSON_NOT_FOUND( 70002, "생성하려는 JSON파일이 존재하지 않습니다."),

    /**
     * DTO Exception
     * */
    ID_INVALID( 80000, "다시 시도해주세요."),
    SOCIAL_TOKEN_NEED( 80000, "소셜 로그인을 진행할 수 없어요. 잠시후 다시 시도해주세요"),
    MEMBER_CREATE_NAME_INVALID( 80000, "마미님의 이름을 다시 설정해주세요."),
    MEMBER_CREATE_RELATION_INVALID( 80000, "아이와의 관계를 다시 설정해주세요."),
    MEMBER_CREATE_AGREE_INVALID( 80000, "약관 동의 항목을 다시 확인해주세요."),
    MEMBER_CREATE_BABY_LIST_INVALID( 80000, "아이를 다시 선택해주세요."),
    MEMBER_CREATE_MAX_BABY_LIST_INVALID( 80000, "아이는 한번에 최대 10명까지 등록할 수 있어요."),
    MEMBER_CREATE_BABY_INFO_INVALID( 80000, "아이에 대한 정보가 잘못되었어요"),

    MEMBER_UPDATE_RELATION_INVALID(80000, "아이와의 관계를 다시 설정해주세요."),
    MEMBER_UPDATE_AGREE_LIST_INVALID(80000, "약관 동의 정보를 확인해주세요."),
    MEMBER_UPDATE_BIRTH_INVALID(80000, "생년월일을 올바르게 입력해주세요."),
    MEMBER_UPDATE_PHONE_INVALID(80000, "휴대폰 번호 형식이 올바르지 않습니다."),
    MEMBER_UPDATE_NAME_INVALID(80000, "이름을 올바르게 입력해주세요."),
    MEMBER_UPDATE_GENDER_INVALID(80000, "성별을 올바르게 입력해주세요."),
    MEMBER_UPDATE_ALARM_LIST_INVALID(80000, "알림 설정을 확인해주세요."),

    BABY_UPDATE_INVALID(80000, "아이 정보를 다시 수정해주세요."),
    BABY_UPDATE_NAME_INVALID(80000, "이름을 올바르게 입력해주세요."),
    BABY_UPDATE_GENDER_INVALID(80000, "아이의 성별을 올바르게 입력해주세요."),
    BABY_UPDATE_BIRTH_INVALID(80000, "아이의 생년월일을 올바르게 입력해주세요."),
    BABY_UPDATE_BIRTH_TIME_INVALID(80000, "아이의 탄생시간을 올바르게 입력해주세요."),
    BABY_UPDATE_ALARM_LIST_INVALID(80000, "알림 설정을 확인해주세요."),
    BABY_UPDATE_TYPE_INVALID(80000, "아이 타입 정보를 다시 수정해주세요."),

    PAGE_TITLE_INVALID( 80000, "페이지 제목 정보를 다시 입력해주세요."),
    PAGE_TYPE_INVALID( 80000, "페이지 타입 정보를 다시 입력해주세요."),
    PAGE_PERIOD_INVALID( 80000, "페이지 기간 정보를 다시 입력해주세요."),
    PAGE_SUMMARY_INVALID( 80000, "페이지 요약 정보를 다시 입력해주세요.");

    private final int code;
    private final String errorMessage;
}
