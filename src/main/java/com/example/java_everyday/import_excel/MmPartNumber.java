package com.example.java_everyday.import_excel;

import lombok.ToString;

@ToString
public class MmPartNumber {
    @ImportValidation(index = 0)
    private String name;
    @ImportValidation(index = 1)
    private int age;
    @ImportValidation(index = 2)
    private double revenue;
    @ImportValidation(index = 3)
    private Long timeStamp;
}
