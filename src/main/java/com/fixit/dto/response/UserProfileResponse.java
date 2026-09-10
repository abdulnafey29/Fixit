package com.fixit.dto.response;

public class UserProfileResponse {

    private Long userId;
    private String name;
    private String username;
    private String email;
    private String role;
    private String phone;
    private String specialization;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long userId, String name, String username, String email, String role, String phone, String specialization) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.specialization = specialization;
    }

    public UserProfileResponse(Long userId, String name, String email, String role, String phone, String specialization) {
        this.userId = userId;
        this.name = name;
        this.username = email != null ? email.split("@")[0] : null;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.specialization = specialization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
