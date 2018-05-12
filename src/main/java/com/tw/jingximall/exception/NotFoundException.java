package com.tw.jingximall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    public NotFoundException(String className, Integer id) {
        super(className +"which id is"+ id + "do not exit!");
    }

    public NotFoundException(String className, String idName1, Long id1, String idName2, Long id2) {

        super(String.format("Cannot find such %s with %s: %d and %s: %d", className, idName1, id1, idName2, id2));

    }
}
