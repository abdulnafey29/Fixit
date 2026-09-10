package com.fixit.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LocationRequest {

    @NotBlank(message = "Block is required")
    private String block;

    @NotBlank(message = "Floor is required")
    private String floor;

    @NotBlank(message = "Room is required")
    private String room;

    private String description;

    public LocationRequest() {
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
