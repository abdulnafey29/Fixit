package com.fixit.controller;

import com.fixit.dto.response.ApiResponse;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.entity.User;
import com.fixit.security.UserPrincipal;
import com.fixit.service.AuthService;
import com.fixit.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/technician")
@PreAuthorize("hasRole('TECHNICIAN')")
public class TechnicianComplaintController {

    @Autowired
    private TechnicianService technicianService;

    @Autowired
    private AuthService authService;

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAssignedComplaints(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ComplaintResponse> complaints = technicianService.getAssignedComplaints(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Assigned complaints retrieved successfully", complaints));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<com.fixit.dto.response.TechnicianStatsResponse>> getTechnicianStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        com.fixit.dto.response.TechnicianStatsResponse stats = technicianService.getTechnicianStats(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Technician statistics retrieved", stats));
    }

    @PutMapping("/complaints/{id}/start")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> startWork(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User technician = authService.getCurrentUser(userPrincipal);
        String comment = (body != null) ? body.get("comment") : null;
        ComplaintDetailResponse response = technicianService.startWork(id, technician, comment);
        return ResponseEntity.ok(ApiResponse.ok("Work status changed to In Progress", response));
    }

    @PutMapping("/complaints/{id}/resolve")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> resolveWork(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User technician = authService.getCurrentUser(userPrincipal);
        String comment = (body != null) ? body.get("comment") : null;
        ComplaintDetailResponse response = technicianService.resolveWork(id, technician, comment);
        return ResponseEntity.ok(ApiResponse.ok("Complaint marked as Resolved", response));
    }
}
