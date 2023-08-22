package com.codestar.HAMI.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageForwardRequest {

    @NotNull
    private Long messageId;

    @NotNull
    private Long chatId;
}
