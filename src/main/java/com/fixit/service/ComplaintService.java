package com.fixit.service;

import com.fixit.dto.request.ComplaintCreateRequest;
import com.fixit.dto.request.FeedbackRequest;
import com.fixit.dto.response.ComplaintDetailResponse;
import com.fixit.dto.response.ComplaintResponse;
import com.fixit.dto.response.FeedbackResponse;
import com.fixit.entity.Complaint;
import com.fixit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComplaintService {

    ComplaintResponse createComplaint(ComplaintCreateRequest request, MultipartFile file, User student);

    List<ComplaintResponse> getStudentComplaints(Long studentId);

    ComplaintDetailResponse getComplaintDetails(Long complaintId, User currentUser);

    FeedbackResponse submitFeedback(Long complaintId, FeedbackRequest feedbackRequest, User student);

    com.fixit.dto.response.FeedbackStatusResponse getFeedbackStatus(Long complaintId, User currentUser);

    com.fixit.dto.response.StudentStatsResponse getStudentStats(Long studentId);

    ComplaintResponse mapToResponse(Complaint complaint);

    ComplaintDetailResponse mapToDetailResponse(Complaint complaint);
}
