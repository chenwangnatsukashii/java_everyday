package com.example.java_everyday.import_excel;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Result {

    private int code;
    private String msg;
    private List<Object> data;

    public static Result buildError() {
        return new Result();
    }
}
