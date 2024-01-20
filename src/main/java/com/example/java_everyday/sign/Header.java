package com.example.java_everyday.sign;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Header {
    private String sign;
    private String appId;
    private String nonce;
    private String timestamp;
    private String appSign;
}
