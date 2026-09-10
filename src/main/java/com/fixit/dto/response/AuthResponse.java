package com.fixit.dto.response;

import com.fixit.enums.Role;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String username;
    private String email;
    private Role role;
    private String specialization;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String name, String username, String email, Role role, String specialization) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.role = role;
        this.specialization = specialization;
    }

    public AuthResponse(String token, Long id, String name, String email, Role role, String specialization) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.username = email != null ? email.split("@")[0] : null;
        this.email = email;
        this.role = role;
        this.specialization = specialization;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
