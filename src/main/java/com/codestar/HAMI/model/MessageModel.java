package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageModel {
    private String text;
    private Instant createdAt;
    private byte[] file;
}
