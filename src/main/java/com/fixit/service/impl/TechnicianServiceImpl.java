package com.fixit.service.impl;

import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.entity.Complaint;
import com.fixit.entity.ComplaintUpdate;
import com.fixit.entity.User;
import com.fixit.enums.ComplaintStatus;
import com.fixit.exception.BadRequestException;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.exception.UnauthorizedException;
import com.fixit.repository.ComplaintRepository;
import com.fixit.repository.ComplaintUpdateRepository;
import com.fixit.service.ComplaintService;
import com.fixit.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintUpdateRepository updateRepository;

    @Autowired
    private ComplaintService complaintService;

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAssignedComplaints(Long technicianId) {
        return complaintRepository.findByAssignedTechnicianIdOrderByCreatedAtDesc(technicianId)
                .stream()
                .map(complaintService::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComplaintDetailResponse startWork(Long complaintId, User technician, String comment) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        if (complaint.getAssignedTechnician() == null || !complaint.getAssignedTechnician().getId().equals(technician.getId())) {
            throw new UnauthorizedException("This complaint is not assigned to you.");
        }

        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new BadRequestException("Complaint is already resolved.");
        }

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        Complaint saved = complaintRepository.save(complaint);

        String note = (comment != null && !comment.trim().isEmpty())
                ? comment.trim()
                : "Technician " + technician.getName() + " started repair work";

        ComplaintUpdate update = new ComplaintUpdate(
                saved,
                technician,
                oldStatus,
                ComplaintStatus.IN_PROGRESS,
                note
        );
        updateRepository.save(update);

        return complaintService.mapToDetailResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse resolveWork(Long complaintId, User technician, String comment) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));

        if (complaint.getAssignedTechnician() == null || !complaint.getAssignedTechnician().getId().equals(technician.getId())) {
            throw new UnauthorizedException("This complaint is not assigned to you.");
        }

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());
        Complaint saved = complaintRepository.save(complaint);

        String note = (comment != null && !comment.trim().isEmpty())
                ? comment.trim()
                : "Issue successfully resolved by " + technician.getName();

        ComplaintUpdate update = new ComplaintUpdate(
                saved,
                technician,
                oldStatus,
                ComplaintStatus.RESOLVED,
                note
        );
        updateRepository.save(update);

        return complaintService.mapToDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public com.fixit.dto.response.TechnicianStatsResponse getTechnicianStats(Long technicianId) {
        long total = complaintRepository.countByAssignedTechnicianId(technicianId);
        long active = complaintRepository.countByAssignedTechnicianIdAndStatusIn(
                technicianId,
                java.util.Arrays.asList(ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS)
        );
        long completed = complaintRepository.countByAssignedTechnicianIdAndStatusIn(
                technicianId,
                java.util.Collections.singletonList(ComplaintStatus.RESOLVED)
        );
        return new com.fixit.dto.response.TechnicianStatsResponse(total, active, completed);
    }
}
