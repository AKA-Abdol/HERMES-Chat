package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.model.AuthenticationResponse;
import com.codestar.HAMI.service.AuthenticationService;
import com.codestar.HAMI.service.ProfileService;
import com.codestar.HAMI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class AuthenticationController {

    @Autowired
    UserService userService;
    @Autowired
    ProfileService profileService;
    @Autowired
    AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> userSignup(
            @RequestBody User user
    ) {
        String token = authenticationService.register(user);
        return ResponseEntity.ok(
                AuthenticationResponse
                        .builder()
                        .hasProfile(false)
                        .token(token)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> userLogin(
            @RequestBody User userData
    ) {
        User user = userService.getUserByEmail(userData.getEmail());
        String token = authenticationService.login(user, userData.getPassword());
        return ResponseEntity.ok(
                AuthenticationResponse
                        .builder()
                        .token(token)
                        .hasProfile(user.hasProfile())
                        .build()
        );
    }
}
