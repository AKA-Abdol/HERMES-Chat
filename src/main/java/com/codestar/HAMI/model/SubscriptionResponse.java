package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.ChatTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionResponse {
    private Long chatId;
    private String name;
    private ChatTypeEnum chatType;
    private byte[] photo;
    private MessagePreview lastMessage;
    private Long unSeenCount;
    private Long profileId;
}
