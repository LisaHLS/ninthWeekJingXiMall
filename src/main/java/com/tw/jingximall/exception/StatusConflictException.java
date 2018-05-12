package com.tw.jingximall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StatusConflictException extends RuntimeException {

    public StatusConflictException(String className, Integer id, String status) {
        super(className + " which id is " + id + " has already been " + status);
    }
}
