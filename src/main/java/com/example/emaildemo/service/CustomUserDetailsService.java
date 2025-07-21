package com.example.emaildemo.service;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Accept either the user’s **username** *or* **email**.
     */
    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        // 1 ⟶ normalise
        String key = input.trim().toLowerCase();

        // 2 ⟶ first try username, then email
        User user =
                userRepository.findByUsernameIgnoreCase(key)
                        .or(() -> userRepository.findByEmailIgnoreCase(key))
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + input)
                        );

        // 3 ⟶ return Spring‑Security object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),          // principal name
                user.getPassword(),          // encoded password
                new ArrayList<>()            // authorities (none for now)
        );
    }
}
