package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatMessagesModel {
    private List<MessageModel> messages;
    private MessageModel pinned;

    public void setPinned(Message message) {
        pinned = MessageModel
                .builder()
                .id(message.getId())
                .text(message.getText())
                .file(message.getFile().getData())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
