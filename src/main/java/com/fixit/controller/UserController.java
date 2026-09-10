package com.fixit.controller;

import com.fixit.dto.response.ApiResponse;
import com.fixit.dto.response.UserProfileResponse;
import com.fixit.entity.User;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.repository.UserRepository;
import com.fixit.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized: Session not found"));
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));

        UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getPhone(),
                user.getSpecialization()
        );

        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", profile));
    }
}
