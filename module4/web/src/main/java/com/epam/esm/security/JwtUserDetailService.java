package com.epam.esm.security;

import com.epam.esm.entity.User;
import com.epam.esm.security.jwt.JwtUserFactory;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JwtUserDetailService implements UserDetailsService {
    private final UserService service;

    @Autowired
    public JwtUserDetailService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = service.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username: " + username + " not found"));
        return JwtUserFactory.create(user);
    }
}
