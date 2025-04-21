package com.dft.mom.web.exception.post;

import lombok.Getter;


@Getter
public class PageException extends RuntimeException {
    private final int code;
    private final String errorMessage;

    public PageException(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
