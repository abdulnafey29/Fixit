package com.fixit.service.impl;

import com.fixit.dto.request.LoginRequest;
import com.fixit.dto.request.RegisterRequest;
import com.fixit.dto.response.AuthResponse;
import com.fixit.entity.User;
import com.fixit.enums.Role;
import com.fixit.exception.BadRequestException;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.repository.UserRepository;
import com.fixit.security.JwtTokenProvider;
import com.fixit.security.UserPrincipal;
import com.fixit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getUsername() != null && !loginRequest.getUsername().isBlank()
                ? loginRequest.getUsername().trim().toLowerCase()
                : (loginRequest.getEmail() != null ? loginRequest.getEmail().trim().toLowerCase() : "");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getSpecialization()
        );
    }

    @Override
    @Transactional
    public AuthResponse registerStudent(RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isBlank()) {
            throw new BadRequestException("Username is required.");
        }
        String username = registerRequest.getUsername().trim().toLowerCase();
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username already exists. Please choose another username.");
        }

        String email = registerRequest.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered. Please login instead.");
        }

        Role roleToAssign = Role.ROLE_STUDENT;
        if (registerRequest.getRole() != null && !registerRequest.getRole().trim().isBlank()) {
            String r = registerRequest.getRole().trim().toUpperCase();
            if (!r.startsWith("ROLE_")) {
                r = "ROLE_" + r;
            }
            try {
                roleToAssign = Role.valueOf(r);
            } catch (IllegalArgumentException e) {
                roleToAssign = Role.ROLE_STUDENT;
            }
        }

        User user = new User();
        user.setName(registerRequest.getName().trim());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(roleToAssign);
        user.setPhone(registerRequest.getPhone());
        if (roleToAssign == Role.ROLE_TECHNICIAN) {
            String spec = registerRequest.getSpecialization();
            user.setSpecialization((spec != null && !spec.trim().isBlank()) ? spec.trim() : "General Maintenance");
        } else if (roleToAssign == Role.ROLE_ADMIN) {
            user.setSpecialization("Administration");
        } else {
            user.setSpecialization(null);
        }

        userRepository.save(user);

        // Auto login after registration
        return login(new LoginRequest(username, registerRequest.getPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new BadRequestException("No authenticated user in context");
        }
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));
    }
}
