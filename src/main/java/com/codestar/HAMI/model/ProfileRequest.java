package com.codestar.HAMI.model;

import lombok.Data;

@Data
public class ProfileRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private Long photoId;
}
