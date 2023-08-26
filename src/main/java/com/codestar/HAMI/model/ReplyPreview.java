package com.codestar.HAMI.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyPreview {
    private String fullName;
    private String text;
}
