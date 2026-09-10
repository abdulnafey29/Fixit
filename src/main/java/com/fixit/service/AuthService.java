package com.fixit.service;

import com.fixit.dto.request.LoginRequest;
import com.fixit.dto.request.RegisterRequest;
import com.fixit.dto.response.AuthResponse;
import com.fixit.entity.User;
import com.fixit.security.UserPrincipal;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse registerStudent(RegisterRequest registerRequest);
    User getCurrentUser(UserPrincipal userPrincipal);
}
