package com.fixit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String block; // e.g. "Block A", "Block B", "Library Building"

    @Column(nullable = false, length = 50)
    private String floor; // e.g. "Ground Floor", "1st Floor", "3rd Floor"

    @Column(nullable = false, length = 50)
    private String room;  // e.g. "Room 304", "Lab 2", "Seminar Hall"

    @Column(length = 255)
    private String description;

    public Location() {
    }

    public Location(String block, String floor, String room, String description) {
        this.block = block;
        this.floor = floor;
        this.room = room;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFormattedLocation() {
        return block + " - " + floor + " (" + room + ")";
    }
}
