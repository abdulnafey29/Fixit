package com.fixit.enums;

public enum ComplaintStatus {
    SUBMITTED("Submitted", "Complaint has been submitted by student"),
    ASSIGNED("Assigned", "Technician has been assigned"),
    IN_PROGRESS("In Progress", "Technician is actively working on the repair"),
    RESOLVED("Resolved", "Issue has been resolved and verified");

    private final String displayName;
    private final String description;

    ComplaintStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
