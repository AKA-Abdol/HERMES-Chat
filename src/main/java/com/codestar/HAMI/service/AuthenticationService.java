package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.model.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public AuthenticationResponse register(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        userService.addUser(user);
        return AuthenticationResponse
                .builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public AuthenticationResponse login(User userData) {
        User user = userService.getUserByEmail(userData.getEmail());
        if (user == null || user.getPassword().equals(passwordEncoder.encode(userData.getPassword())))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String token = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(token)
                .build();
    }

}
