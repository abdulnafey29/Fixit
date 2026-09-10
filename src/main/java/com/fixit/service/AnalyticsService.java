package com.fixit.service;

import com.fixit.dto.response.AnalyticsSummaryResponse;

import java.util.Map;

public interface AnalyticsService {

    AnalyticsSummaryResponse getSummary();

    Map<String, Long> getComplaintsByCategory();

    Map<String, Long> getComplaintsByLocation();
}
