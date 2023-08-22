package com.codestar.HAMI.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "chat")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatElasticModel {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "username")
    private String username;


    @Field(type = FieldType.Text, name = "chatType")
    private String chatType;

    private byte[] photo;

    @Field(type = FieldType.Text, name = "fullName")
    private String fullName;
}
