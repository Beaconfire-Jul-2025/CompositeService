package org.beaconfire.composite.controller;

import lombok.RequiredArgsConstructor;
import org.beaconfire.composite.dto.FacilityReportDTO;
import org.beaconfire.composite.dto.PageListResponse;
import org.beaconfire.composite.service.FacilityReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facility-report")
@RequiredArgsConstructor
public class FacilityReportController {
    private final FacilityReportService facilityReportService;

    @GetMapping
    public PageListResponse<FacilityReportDTO> getFacilityReports(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize) {
        // Get userId from Spring Security context
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return facilityReportService.getFacilityReportsForEmployee(userId, current, pageSize);
    }
}
