package com.fixit.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignTechnicianRequest {

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    private String comment;

    public AssignTechnicianRequest() {
    }

    public AssignTechnicianRequest(Long technicianId, String comment) {
        this.technicianId = technicianId;
        this.comment = comment;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
