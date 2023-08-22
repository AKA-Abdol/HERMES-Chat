package com.codestar.HAMI.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChatSubscriberResponse {
    private byte[] photo;
    private String fullName;
}
