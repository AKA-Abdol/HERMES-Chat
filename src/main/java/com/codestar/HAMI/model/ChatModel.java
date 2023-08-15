package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.ChatTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatModel {
    private long chatId;

    private String bio;

    private ChatTypeEnum chatType;

    private String description;

    private byte[] photo;

}