package com.fixit.service;

import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.entity.User;

import java.util.List;

public interface TechnicianService {

    List<ComplaintResponse> getAssignedComplaints(Long technicianId);

    ComplaintDetailResponse startWork(Long complaintId, User technician, String comment);

    ComplaintDetailResponse resolveWork(Long complaintId, User technician, String comment);

    com.fixit.dto.response.TechnicianStatsResponse getTechnicianStats(Long technicianId);
}
