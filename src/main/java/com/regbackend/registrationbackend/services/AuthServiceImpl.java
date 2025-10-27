package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.entity.UserEntity;
import com.regbackend.registrationbackend.repository.UserRepository;
import com.regbackend.registrationbackend.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserEntity registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role("USER")
                .build();

        return userRepository.save(user);
    }

    @Override
    public String loginUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtTokenUtil.generateToken(user.getEmail());
    }
}
