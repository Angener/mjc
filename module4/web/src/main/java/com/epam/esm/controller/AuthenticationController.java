package com.epam.esm.controller;

import com.epam.esm.entity.User;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.security.jwt.JwtProvider;
import com.epam.esm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(UserService userService, JwtProvider jwtProvider,
                                    AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signUp")
    public User register(@RequestBody User user) {
        try {
            return userService.save(user);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40006", 40006, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return getJwtToken(user);
    }

    private ResponseEntity<?> getJwtToken(User user) {
        String username = user.getName();
        authenticate(user);
        user = userService.findByName(username).orElseThrow(() ->
                new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND));
        String token = jwtProvider.createToken(username, user.getRoles());
        return makeResponse(username, token);
    }

    private void authenticate(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
    }

    private ResponseEntity<?> makeResponse(String username, String token) {
        Map<Object, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
