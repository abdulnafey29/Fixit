package com.fixit.controller;

import com.fixit.dto.request.AssignTechnicianRequest;
import com.fixit.dto.request.StatusUpdateRequest;
import com.fixit.dto.request.TechnicianCreateRequest;
import com.fixit.dto.response.ApiResponse;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.TechnicianWorkloadResponse;
import com.fixit.entity.User;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import com.fixit.enums.Role;
import com.fixit.security.UserPrincipal;
import com.fixit.service.AdminService;
import com.fixit.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminComplaintController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints(
            @RequestParam(value = "status", required = false) ComplaintStatus status,
            @RequestParam(value = "priority", required = false) Priority priority,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "search", required = false) String search
    ) {
        List<ComplaintResponse> complaints = adminService.filterComplaints(status, priority, categoryId, locationId, search);
        return ResponseEntity.ok(ApiResponse.ok("Complaints retrieved successfully", complaints));
    }

    @PutMapping("/complaints/{id}/assign")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> assignTechnician(
            @PathVariable("id") Long id,
            @Valid @RequestBody AssignTechnicianRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User admin = authService.getCurrentUser(userPrincipal);
        ComplaintDetailResponse response = adminService.assignTechnician(id, request, admin);
        return ResponseEntity.ok(ApiResponse.ok("Complaint assigned to technician successfully", response));
    }

    @PutMapping("/complaints/{id}/priority")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> updatePriority(
            @PathVariable("id") Long id,
            @RequestParam("priority") Priority priority,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User admin = authService.getCurrentUser(userPrincipal);
        ComplaintDetailResponse response = adminService.updatePriority(id, priority, admin);
        return ResponseEntity.ok(ApiResponse.ok("Complaint priority updated to " + priority.name(), response));
    }

    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User admin = authService.getCurrentUser(userPrincipal);
        ComplaintDetailResponse response = adminService.updateStatus(id, request, admin);
        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated successfully", response));
    }

    @GetMapping("/technicians")
    public ResponseEntity<ApiResponse<List<TechnicianWorkloadResponse>>> getTechnicians() {
        List<TechnicianWorkloadResponse> technicians = adminService.getTechniciansWithWorkload();
        return ResponseEntity.ok(ApiResponse.ok("Technicians retrieved successfully", technicians));
    }

    @PostMapping("/technicians")
    public ResponseEntity<ApiResponse<User>> createTechnician(@Valid @RequestBody TechnicianCreateRequest request) {
        User technician = adminService.createTechnician(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Technician created successfully", technician));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(value = "role", required = false) Role role
    ) {
        List<User> users = adminService.getAllUsers(role);
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved successfully", users));
    }
}
