package com.codestar.HAMI.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String firstName;
    private String lastName;
    private String bio;
    private byte[] picture;
}
