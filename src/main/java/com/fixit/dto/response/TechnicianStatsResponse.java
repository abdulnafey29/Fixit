package com.fixit.dto.response;

public class TechnicianStatsResponse {

    private long totalAssigned;
    private long active;
    private long completed;

    public TechnicianStatsResponse() {
    }

    public TechnicianStatsResponse(long totalAssigned, long active, long completed) {
        this.totalAssigned = totalAssigned;
        this.active = active;
        this.completed = completed;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(long totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public long getActive() {
        return active;
    }

    public void setActive(long active) {
        this.active = active;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
