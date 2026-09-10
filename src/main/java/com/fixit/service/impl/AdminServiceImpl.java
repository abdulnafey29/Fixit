package com.fixit.service.impl;

import com.fixit.dto.request.AssignTechnicianRequest;
import com.fixit.dto.request.StatusUpdateRequest;
import com.fixit.dto.request.TechnicianCreateRequest;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.TechnicianWorkloadResponse;
import com.fixit.entity.Complaint;
import com.fixit.entity.ComplaintUpdate;
import com.fixit.entity.User;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import com.fixit.enums.Role;
import com.fixit.exception.BadRequestException;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.repository.ComplaintRepository;
import com.fixit.repository.ComplaintUpdateRepository;
import com.fixit.repository.UserRepository;
import com.fixit.service.AdminService;
import com.fixit.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintUpdateRepository updateRepository;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> filterComplaints(
            ComplaintStatus status,
            Priority priority,
            Long categoryId,
            Long locationId,
            String search
    ) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return complaintRepository.filterComplaints(status, priority, categoryId, cleanSearch)
                .stream()
                .map(complaintService::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComplaintDetailResponse assignTechnician(Long complaintId, AssignTechnicianRequest request, User admin) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + request.getTechnicianId()));

        if (technician.getRole() != Role.ROLE_TECHNICIAN) {
            throw new BadRequestException("Selected user is not a technician.");
        }

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setAssignedTechnician(technician);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        Complaint saved = complaintRepository.save(complaint);

        String note = (request.getComment() != null && !request.getComment().trim().isEmpty())
                ? request.getComment().trim()
                : "Assigned to technician " + technician.getName();

        ComplaintUpdate update = new ComplaintUpdate(
                saved,
                admin,
                oldStatus,
                ComplaintStatus.ASSIGNED,
                note
        );
        updateRepository.save(update);

        return complaintService.mapToDetailResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse updatePriority(Long complaintId, Priority priority, User admin) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        Priority oldPriority = complaint.getPriority();
        complaint.setPriority(priority);
        Complaint saved = complaintRepository.save(complaint);

        ComplaintUpdate update = new ComplaintUpdate(
                saved,
                admin,
                complaint.getStatus(),
                complaint.getStatus(),
                "Priority modified from " + oldPriority.name() + " to " + priority.name() + " by Admin"
        );
        updateRepository.save(update);

        return complaintService.mapToDetailResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse updateStatus(Long complaintId, StatusUpdateRequest request, User admin) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(request.getStatus());
        if (request.getStatus() == ComplaintStatus.RESOLVED && complaint.getResolvedAt() == null) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint saved = complaintRepository.save(complaint);

        String note = (request.getComment() != null && !request.getComment().trim().isEmpty())
                ? request.getComment().trim()
                : "Status manually updated to " + request.getStatus().name() + " by Admin";

        ComplaintUpdate update = new ComplaintUpdate(
                saved,
                admin,
                oldStatus,
                request.getStatus(),
                note
        );
        updateRepository.save(update);

        return complaintService.mapToDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianWorkloadResponse> getTechniciansWithWorkload() {
        List<User> technicians = userRepository.findByRoleOrderByNameAsc(Role.ROLE_TECHNICIAN);
        return technicians.stream().map(tech -> {
            long active = complaintRepository.countByAssignedTechnicianIdAndStatusIn(
                    tech.getId(),
                    Arrays.asList(ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS)
            );
            long completed = complaintRepository.countByAssignedTechnicianIdAndStatusIn(
                    tech.getId(),
                    Collections.singletonList(ComplaintStatus.RESOLVED)
            );
            return new TechnicianWorkloadResponse(
                    tech.getId(),
                    tech.getName(),
                    tech.getEmail(),
                    tech.getPhone(),
                    tech.getSpecialization(),
                    active,
                    completed
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User createTechnician(TechnicianCreateRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }

        String username = request.getUsername() != null && !request.getUsername().isBlank()
                ? request.getUsername().trim().toLowerCase()
                : request.getEmail().split("@")[0].trim().toLowerCase();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username already exists: " + username);
        }

        User technician = new User();
        technician.setName(request.getName().trim());
        technician.setUsername(username);
        technician.setEmail(email);
        technician.setPassword(passwordEncoder.encode(request.getPassword()));
        technician.setRole(Role.ROLE_TECHNICIAN);
        technician.setPhone(request.getPhone());
        technician.setSpecialization(request.getSpecialization());

        return userRepository.save(technician);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers(Role role) {
        if (role != null) {
            return userRepository.findByRole(role);
        }
        return userRepository.findAll();
    }
}
