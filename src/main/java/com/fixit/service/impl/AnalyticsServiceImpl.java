package com.fixit.service.impl;

import com.fixit.dto.response.AnalyticsSummaryResponse;
import com.fixit.enums.ComplaintStatus;
import com.fixit.enums.Priority;
import com.fixit.repository.ComplaintRepository;
import com.fixit.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummary() {
        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();

        long total = complaintRepository.count();
        long submitted = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED);
        long assigned = complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);

        long high = complaintRepository.countByPriority(Priority.HIGH);
        long critical = complaintRepository.countByPriority(Priority.CRITICAL);

        summary.setTotalComplaints(total);
        summary.setPendingComplaints(submitted);
        summary.setAssignedComplaints(assigned);
        summary.setInProgressComplaints(inProgress);
        summary.setResolvedComplaints(resolved);
        summary.setHighPriorityComplaints(high + critical);

        double rate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;
        summary.setResolutionRatePercentage(Math.round(rate * 10.0) / 10.0);

        List<com.fixit.entity.Complaint> resolvedList = complaintRepository.findByStatus(ComplaintStatus.RESOLVED);
        double avgHours = resolvedList.stream()
                .filter(c -> c.getResolvedAt() != null && c.getCreatedAt() != null)
                .mapToLong(c -> java.time.Duration.between(c.getCreatedAt(), c.getResolvedAt()).toMinutes())
                .average()
                .orElse(0.0) / 60.0;
        summary.setAverageResolutionTimeHours(Math.round(avgHours * 10.0) / 10.0);

        summary.setComplaintsByCategory(getComplaintsByCategory());
        summary.setComplaintsByLocation(getComplaintsByLocation());

        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("Submitted", submitted);
        statusMap.put("Assigned", assigned);
        statusMap.put("In Progress", inProgress);
        statusMap.put("Resolved", resolved);
        summary.setComplaintsByStatus(statusMap);

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getComplaintsByCategory() {
        Map<String, Long> map = new LinkedHashMap<>();
        List<Object[]> results = complaintRepository.countComplaintsByCategory();
        for (Object[] row : results) {
            String category = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            map.put(category, count);
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getComplaintsByLocation() {
        Map<String, Long> map = new LinkedHashMap<>();
        List<Object[]> results = complaintRepository.countComplaintsByBlock();
        for (Object[] row : results) {
            String block = row[0] != null ? row[0].toString() : "Unknown";
            Long count = ((Number) row[1]).longValue();
            map.put(block, count);
        }
        return map;
    }
}
