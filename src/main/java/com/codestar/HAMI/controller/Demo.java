package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Demo {

    @Autowired
    UserAuthenticationService userAuthenticationService;
    @RequestMapping("/hello/{name}")
    public ResponseEntity<String> hello(@PathVariable String name){
        return ResponseEntity.ok(
                userAuthenticationService
                        .getAuthenticatedUser()
                        .getEmail()
        );
    }
}
