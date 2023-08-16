package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAuthenticationService {
    public User getAuthenticatedUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found!");

        return user;
    }

    public Profile getAuthenticatedProfile() {
        User user = getAuthenticatedUser();
        return user.getProfile(); // when a user is valid but profile not found!
    }
}
