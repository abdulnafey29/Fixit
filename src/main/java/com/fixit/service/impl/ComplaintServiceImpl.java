package com.fixit.service.impl;

import com.fixit.dto.request.ComplaintCreateRequest;
import com.fixit.dto.request.FeedbackRequest;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.ComplaintUpdateResponse;
import com.fixit.dto.response.FeedbackResponse;
import com.fixit.dto.response.FeedbackStatusResponse;
import com.fixit.dto.response.StudentStatsResponse;
import com.fixit.entity.*;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import com.fixit.enums.Role;
import com.fixit.exception.BadRequestException;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.exception.UnauthorizedException;
import com.fixit.repository.*;
import com.fixit.service.ComplaintService;
import com.fixit.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ComplaintUpdateRepository updateRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request, MultipartFile file, User student) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        String locationText = null;
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            locationText = request.getLocation().trim();
        } else if (request.getLocationId() != null) {
            Location loc = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + request.getLocationId()));
            locationText = loc.getFormattedLocation();
        } else {
            throw new IllegalArgumentException("Campus location is required");
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setCategory(category);
        complaint.setLocation(locationText);
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        complaint.setStatus(ComplaintStatus.SUBMITTED);
        complaint.setStudent(student);

        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(file);
            complaint.setImageUrl(imageUrl);
        }

        // Add initial audit record
        Complaint savedComplaint = complaintRepository.save(complaint);

        ComplaintUpdate initialUpdate = new ComplaintUpdate(
                savedComplaint,
                student,
                null,
                ComplaintStatus.SUBMITTED,
                "Complaint successfully submitted by " + student.getName()
        );
        updateRepository.save(initialUpdate);

        return mapToResponse(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getStudentComplaints(Long studentId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintDetailResponse getComplaintDetails(Long complaintId, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        // Strict role-based isolation check:
        // 1. Students can ONLY view their own complaints
        if (currentUser.getRole() == Role.ROLE_STUDENT && !complaint.getStudent().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Access denied: You are not authorized to view this complaint.");
        }

        // 2. Technicians can ONLY view complaints assigned to them
        if (currentUser.getRole() == Role.ROLE_TECHNICIAN) {
            if (complaint.getAssignedTechnician() == null || !complaint.getAssignedTechnician().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Access denied: This complaint is not assigned to you.");
            }
        }

        return mapToDetailResponse(complaint);
    }

    @Override
    @Transactional
    public FeedbackResponse submitFeedback(Long complaintId, FeedbackRequest request, User student) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Only the student who submitted the complaint can provide feedback.");
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            throw new BadRequestException("Feedback can only be submitted after the issue is marked as Resolved.");
        }

        if (feedbackRepository.existsByComplaintId(complaintId)) {
            throw new BadRequestException("Feedback has already been submitted for this complaint.");
        }

        Feedback feedback = new Feedback(complaint, student, request.getRating(), request.getComment());
        Feedback saved = feedbackRepository.save(feedback);

        ComplaintUpdate feedbackUpdate = new ComplaintUpdate(
                complaint,
                student,
                complaint.getStatus(),
                complaint.getStatus(),
                "Student submitted resolution rating: " + request.getRating() + " / 5 stars."
        );
        updateRepository.save(feedbackUpdate);

        FeedbackResponse response = new FeedbackResponse();
        response.setId(saved.getId());
        response.setStudentId(student.getId());
        response.setStudentName(student.getName());
        response.setRating(saved.getRating());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackStatusResponse getFeedbackStatus(Long complaintId, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        // Students can only check feedback for their own complaints
        if (currentUser.getRole() == Role.ROLE_STUDENT && !complaint.getStudent().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Access denied: You are not authorized to view feedback for this complaint.");
        }

        // Technicians can only check feedback for assigned complaints
        if (currentUser.getRole() == Role.ROLE_TECHNICIAN) {
            if (complaint.getAssignedTechnician() == null || !complaint.getAssignedTechnician().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Access denied: You are not assigned to this complaint.");
            }
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            return FeedbackStatusResponse.notSubmitted();
        }

        return feedbackRepository.findByComplaintId(complaintId)
                .map(fb -> FeedbackStatusResponse.submitted(
                        fb.getRating(),
                        fb.getComment(),
                        fb.getCreatedAt(),
                        fb.getStudent().getName()
                ))
                .orElseGet(FeedbackStatusResponse::notSubmitted);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentStatsResponse getStudentStats(Long studentId) {
        long total = complaintRepository.countByStudentId(studentId);
        long submitted = complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.SUBMITTED);
        long assigned = complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.ASSIGNED);
        long inProgress = complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStudentIdAndStatus(studentId, ComplaintStatus.RESOLVED);

        return new StudentStatsResponse(total, submitted, assigned, inProgress, resolved);
    }

    @Override
    public ComplaintResponse mapToResponse(Complaint c) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setCategoryId(c.getCategory().getId());
        dto.setCategoryName(c.getCategory().getName());
        dto.setCategoryIcon(c.getCategory().getIcon());
        dto.setLocation(c.getLocation());
        dto.setLocationDisplay(c.getLocation());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        dto.setStudentId(c.getStudent().getId());
        dto.setStudentName(c.getStudent().getName());
        if (c.getAssignedTechnician() != null) {
            dto.setAssignedTechnicianId(c.getAssignedTechnician().getId());
            dto.setAssignedTechnicianName(c.getAssignedTechnician().getName());
        }
        dto.setImageUrl(c.getImageUrl());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setResolvedAt(c.getResolvedAt());
        return dto;
    }

    @Override
    public ComplaintDetailResponse mapToDetailResponse(Complaint c) {
        ComplaintDetailResponse dto = new ComplaintDetailResponse();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setCategoryId(c.getCategory().getId());
        dto.setCategoryName(c.getCategory().getName());
        dto.setCategoryIcon(c.getCategory().getIcon());
        dto.setLocation(c.getLocation());
        dto.setLocationDisplay(c.getLocation());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        dto.setStudentId(c.getStudent().getId());
        dto.setStudentName(c.getStudent().getName());
        if (c.getAssignedTechnician() != null) {
            dto.setAssignedTechnicianId(c.getAssignedTechnician().getId());
            dto.setAssignedTechnicianName(c.getAssignedTechnician().getName());
        }
        dto.setImageUrl(c.getImageUrl());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setResolvedAt(c.getResolvedAt());

        List<ComplaintUpdateResponse> updateResponses = updateRepository.findByComplaintIdOrderByCreatedAtAsc(c.getId())
                .stream()
                .map(u -> {
                    ComplaintUpdateResponse ur = new ComplaintUpdateResponse();
                    ur.setId(u.getId());
                    ur.setUpdatedById(u.getUpdatedBy().getId());
                    ur.setUpdatedByName(u.getUpdatedBy().getName());
                    ur.setUpdatedByRole(u.getUpdatedBy().getRole().name());
                    ur.setOldStatus(u.getOldStatus());
                    ur.setNewStatus(u.getNewStatus());
                    ur.setComment(u.getComment());
                    ur.setCreatedAt(u.getCreatedAt());
                    return ur;
                })
                .collect(Collectors.toList());
        dto.setUpdates(updateResponses);

        feedbackRepository.findByComplaintId(c.getId()).ifPresent(fb -> {
            FeedbackResponse fr = new FeedbackResponse();
            fr.setId(fb.getId());
            fr.setStudentId(fb.getStudent().getId());
            fr.setStudentName(fb.getStudent().getName());
            fr.setRating(fb.getRating());
            fr.setComment(fb.getComment());
            fr.setCreatedAt(fb.getCreatedAt());
            dto.setFeedback(fr);
        });

        return dto;
    }
}
