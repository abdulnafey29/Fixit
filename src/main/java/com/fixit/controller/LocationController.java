package com.fixit.controller;

import com.fixit.dto.request.LocationRequest;
import com.fixit.dto.response.ApiResponse;
import com.fixit.entity.Location;
import com.fixit.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.ok("Locations retrieved", locations));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Location>> createLocation(@Valid @RequestBody LocationRequest request) {
        Location location = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Location created successfully", location));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable("id") Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.ok("Location deleted successfully"));
    }
}
