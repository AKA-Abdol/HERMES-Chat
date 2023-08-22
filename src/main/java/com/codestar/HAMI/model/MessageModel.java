package com.codestar.HAMI.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageModel {
    private Long id;
    private String text;
    private Instant createdAt;
    private byte[] file;
    private String fullName;
    private Boolean forwarded;
}
