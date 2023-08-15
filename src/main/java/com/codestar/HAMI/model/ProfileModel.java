package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.Message;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ProfileModel {

    private String username;
    private String firstName;
    private String lastName;
    private String bio;
    private byte[] picture;
    private User user;
    private Set<Subscription> subscriptions;
    private Set<Message> messages;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
