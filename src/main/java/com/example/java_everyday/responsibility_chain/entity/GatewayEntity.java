package com.example.java_everyday.responsibility_chain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatewayEntity {
    private Integer handlerId;
    private String name;
    private String conference;
    private Integer preHandlerId;
    private Integer nextHandlerId;
}
