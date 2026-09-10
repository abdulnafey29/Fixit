package com.fixit.dto.response;

public class TechnicianWorkloadResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private long activeTasksCount;
    private long completedTasksCount;

    public TechnicianWorkloadResponse() {
    }

    public TechnicianWorkloadResponse(Long id, String name, String email, String phone, String specialization, long activeTasksCount, long completedTasksCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.activeTasksCount = activeTasksCount;
        this.completedTasksCount = completedTasksCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public long getActiveTasksCount() {
        return activeTasksCount;
    }

    public void setActiveTasksCount(long activeTasksCount) {
        this.activeTasksCount = activeTasksCount;
    }

    public long getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void setCompletedTasksCount(long completedTasksCount) {
        this.completedTasksCount = completedTasksCount;
    }
}
