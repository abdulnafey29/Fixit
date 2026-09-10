package com.fixit.service.impl;

import com.fixit.dto.request.LocationRequest;
import com.fixit.entity.Location;
import com.fixit.exception.ResourceNotFoundException;
import com.fixit.repository.LocationRepository;
import com.fixit.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @Override
    @Transactional
    public Location createLocation(LocationRequest request) {
        Location location = new Location();
        location.setBlock(request.getBlock().trim());
        location.setFloor(request.getFloor().trim());
        location.setRoom(request.getRoom().trim());
        location.setDescription(request.getDescription());
        return locationRepository.save(location);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}
