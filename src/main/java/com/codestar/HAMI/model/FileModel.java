package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.FileTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileModel {
    private Long id;

    private String ContentType;

    private byte[] data;
}
