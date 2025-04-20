package com.dft.mom.web.exception.member;

import lombok.Getter;


@Getter
public class ExcelException extends RuntimeException {
    private final int code;
    private final String errorMessage;

    public ExcelException(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
