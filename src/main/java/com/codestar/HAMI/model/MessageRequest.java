package com.codestar.HAMI.model;

import lombok.Data;

@Data
public class MessageRequest {
    private String text;
    private Long fileId;
}
