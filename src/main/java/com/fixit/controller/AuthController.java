package com.fixit.controller;

import com.fixit.dto.request.LoginRequest;
import com.fixit.dto.request.RegisterRequest;
import com.fixit.dto.response.ApiResponse;
import com.fixit.dto.response.AuthResponse;
import com.fixit.entity.User;
import com.fixit.security.UserPrincipal;
import com.fixit.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.registerStudent(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = authService.getCurrentUser(userPrincipal);
        AuthResponse response = new AuthResponse(
                null,
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getSpecialization()
        );
        return ResponseEntity.ok(ApiResponse.ok("Current user profile retrieved", response));
    }
}
