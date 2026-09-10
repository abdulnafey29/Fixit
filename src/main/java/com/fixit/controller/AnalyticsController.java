package com.fixit.controller;

import com.fixit.dto.response.AnalyticsSummaryResponse;
import com.fixit.dto.response.ApiResponse;
import com.fixit.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary() {
        AnalyticsSummaryResponse summary = analyticsService.getSummary();
        return ResponseEntity.ok(ApiResponse.ok("Analytics summary retrieved", summary));
    }

    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getByCategory() {
        Map<String, Long> map = analyticsService.getComplaintsByCategory();
        return ResponseEntity.ok(ApiResponse.ok("Category distribution retrieved", map));
    }

    @GetMapping("/by-location")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getByLocation() {
        Map<String, Long> map = analyticsService.getComplaintsByLocation();
        return ResponseEntity.ok(ApiResponse.ok("Location distribution retrieved", map));
    }
}
