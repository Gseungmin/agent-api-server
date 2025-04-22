package com.dft.mom.web.exception.handler;

import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.ErrorResult;
import com.dft.mom.web.exception.excel.ExcelException;
import com.dft.mom.web.exception.member.FamilyException;
import com.dft.mom.web.exception.member.MemberException;
import com.dft.mom.web.exception.post.PageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExcelException.class)
    public ErrorResult excelExceptionHandle(ExcelException e, HttpServletRequest request) {
        log.error("[ExcelException] url: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getMessage(), e.getCause());
        return new ErrorResult(e.getCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MemberException.class)
    public ErrorResult excelExceptionHandle(MemberException e, HttpServletRequest request) {
        log.error("[MemberException] url: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getMessage(), e.getCause());
        return new ErrorResult(e.getCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FamilyException.class)
    public ErrorResult excelExceptionHandle(FamilyException e, HttpServletRequest request) {
        log.error("[FamilyException] url: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getMessage(), e.getCause());
        return new ErrorResult(e.getCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PageException.class)
    public ErrorResult excelExceptionHandle(PageException e, HttpServletRequest request) {
        log.error("[PageException] url: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getMessage(), e.getCause());
        return new ErrorResult(e.getCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommonException.class)
    public ErrorResult excelExceptionHandle(CommonException e, HttpServletRequest request) {
        log.error("[CommonException] url: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getMessage(), e.getCause());
        return new ErrorResult(e.getCode(), e.getErrorMessage());
    }
}
