package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.entity.UserEntity;

public interface AuthService {
    UserEntity registerUser(String email, String password);
    String loginUser(String email, String password);
}

