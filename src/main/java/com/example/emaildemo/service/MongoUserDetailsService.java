package com.example.emaildemo.service;

import com.example.emaildemo.entity_model.User;
import com.example.emaildemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(input)
                .or(() -> userRepository.findByEmailIgnoreCase(input))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + input));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
