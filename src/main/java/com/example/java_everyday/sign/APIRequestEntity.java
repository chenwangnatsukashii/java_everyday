package com.example.java_everyday.sign;

import lombok.Data;

@Data
public class APIRequestEntity {
    private Header header;
    private UserEntity body;
}
