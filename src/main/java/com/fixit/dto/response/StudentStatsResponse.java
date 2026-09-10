package com.fixit.dto.response;

public class StudentStatsResponse {

    private long total;
    private long submitted;
    private long assigned;
    private long inProgress;
    private long resolved;

    public StudentStatsResponse() {
    }

    public StudentStatsResponse(long total, long submitted, long assigned, long inProgress, long resolved) {
        this.total = total;
        this.submitted = submitted;
        this.assigned = assigned;
        this.inProgress = inProgress;
        this.resolved = resolved;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getResolved() {
        return resolved;
    }

    public void setResolved(long resolved) {
        this.resolved = resolved;
    }
}
