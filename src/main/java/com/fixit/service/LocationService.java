package com.fixit.service;

import com.fixit.dto.request.LocationRequest;
import com.fixit.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();
    Location getLocationById(Long id);
    Location createLocation(LocationRequest request);
    void deleteLocation(Long id);
}
