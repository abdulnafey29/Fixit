package com.fixit.service;

import com.fixit.dto.request.AssignTechnicianRequest;
import com.fixit.dto.request.StatusUpdateRequest;
import com.fixit.dto.request.TechnicianCreateRequest;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.TechnicianWorkloadResponse;
import com.fixit.entity.User;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import com.fixit.enums.Role;

import java.util.List;

public interface AdminService {

    List<ComplaintResponse> filterComplaints(
            ComplaintStatus status,
            Priority priority,
            Long categoryId,
            Long locationId,
            String search
    );

    ComplaintDetailResponse assignTechnician(Long complaintId, AssignTechnicianRequest request, User admin);

    ComplaintDetailResponse updatePriority(Long complaintId, Priority priority, User admin);

    ComplaintDetailResponse updateStatus(Long complaintId, StatusUpdateRequest request, User admin);

    List<TechnicianWorkloadResponse> getTechniciansWithWorkload();

    User createTechnician(TechnicianCreateRequest request);

    List<User> getAllUsers(Role role);
}
