package com.fixit.controller;

import com.fixit.dto.request.ComplaintCreateRequest;
import com.fixit.dto.request.FeedbackRequest;
import com.fixit.dto.response.ApiResponse;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.FeedbackResponse;
import com.fixit.entity.User;
import com.fixit.enums.Priority;
import com.fixit.security.UserPrincipal;
import com.fixit.service.AuthService;
import com.fixit.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private AuthService authService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "priority", required = false) String priorityStr,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User student = authService.getCurrentUser(userPrincipal);

        ComplaintCreateRequest request = new ComplaintCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setLocation(location);
        request.setLocationId(locationId);

        if (priorityStr != null && !priorityStr.isEmpty()) {
            try {
                request.setPriority(Priority.valueOf(priorityStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                request.setPriority(Priority.MEDIUM);
            }
        } else {
            request.setPriority(Priority.MEDIUM);
        }

        ComplaintResponse response = complaintService.createComplaint(request, image, student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Complaint registered successfully with ID #" + response.getId(), response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ComplaintResponse> complaints = complaintService.getStudentComplaints(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Complaints retrieved successfully", complaints));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<ComplaintDetailResponse>> getComplaintDetails(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User currentUser = authService.getCurrentUser(userPrincipal);
        ComplaintDetailResponse detail = complaintService.getComplaintDetails(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Complaint details retrieved", detail));
    }

    @GetMapping("/my/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<com.fixit.dto.response.StudentStatsResponse>> getMyStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        com.fixit.dto.response.StudentStatsResponse stats = complaintService.getStudentStats(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Student statistics retrieved", stats));
    }

    @GetMapping("/{id}/feedback")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<com.fixit.dto.response.FeedbackStatusResponse>> getFeedbackStatus(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User currentUser = authService.getCurrentUser(userPrincipal);
        com.fixit.dto.response.FeedbackStatusResponse status = complaintService.getFeedbackStatus(id, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Feedback status retrieved", status));
    }

    @PostMapping("/{id}/feedback")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @PathVariable("id") Long id,
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User student = authService.getCurrentUser(userPrincipal);
        FeedbackResponse response = complaintService.submitFeedback(id, feedbackRequest, student);
        return ResponseEntity.ok(ApiResponse.ok("Thank you! Your feedback has been recorded.", response));
    }
}
