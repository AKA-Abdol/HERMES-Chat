package com.codestar.HAMI.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessagePreview {
    private String data;
    private Instant sentAt;
}
