package com.codestar.HAMI.elasticsearch.model;

import com.codestar.HAMI.entity.ChatTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "profile")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileElasticModel {

    @Id
    private Long id;
    private String username;
    @Enumerated(EnumType.STRING)
    private ChatTypeEnum chatType;
    private byte[] photo;
}
