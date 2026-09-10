package com.fixit.dto.response;

import java.util.Map;

public class AnalyticsSummaryResponse {

    private long totalComplaints;
    private long pendingComplaints;     // SUBMITTED
    private long assignedComplaints;    // ASSIGNED
    private long inProgressComplaints;   // IN_PROGRESS
    private long resolvedComplaints;     // RESOLVED
    private long highPriorityComplaints; // HIGH + CRITICAL
    private double resolutionRatePercentage;
    private double averageResolutionTimeHours;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByLocation;
    private Map<String, Long> complaintsByStatus;

    public AnalyticsSummaryResponse() {
    }

    public double getAverageResolutionTimeHours() {
        return averageResolutionTimeHours;
    }

    public void setAverageResolutionTimeHours(double averageResolutionTimeHours) {
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public long getAssignedComplaints() {
        return assignedComplaints;
    }

    public void setAssignedComplaints(long assignedComplaints) {
        this.assignedComplaints = assignedComplaints;
    }

    public long getInProgressComplaints() {
        return inProgressComplaints;
    }

    public void setInProgressComplaints(long inProgressComplaints) {
        this.inProgressComplaints = inProgressComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getHighPriorityComplaints() {
        return highPriorityComplaints;
    }

    public void setHighPriorityComplaints(long highPriorityComplaints) {
        this.highPriorityComplaints = highPriorityComplaints;
    }

    public double getResolutionRatePercentage() {
        return resolutionRatePercentage;
    }

    public void setResolutionRatePercentage(double resolutionRatePercentage) {
        this.resolutionRatePercentage = resolutionRatePercentage;
    }

    public Map<String, Long> getComplaintsByCategory() {
        return complaintsByCategory;
    }

    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) {
        this.complaintsByCategory = complaintsByCategory;
    }

    public Map<String, Long> getComplaintsByLocation() {
        return complaintsByLocation;
    }

    public void setComplaintsByLocation(Map<String, Long> complaintsByLocation) {
        this.complaintsByLocation = complaintsByLocation;
    }

    public Map<String, Long> getComplaintsByStatus() {
        return complaintsByStatus;
    }

    public void setComplaintsByStatus(Map<String, Long> complaintsByStatus) {
        this.complaintsByStatus = complaintsByStatus;
    }
}
