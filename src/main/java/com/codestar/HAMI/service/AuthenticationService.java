package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.model.AuthenticationResponse;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private String hash(String value) {
        return Hashing
                .sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
    }

    public String register(User user) {
        String password = user.getPassword();
        user.setPassword(hash(password));
        userService.addUser(user);
        return jwtService.generateToken(user);
    }

    public String login(User user, String password) {
        if (user == null || !user.getPassword().equals(hash(password)))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return jwtService.generateToken(user);
    }

}
