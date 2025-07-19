package org.beaconfire.composite.service;

import lombok.RequiredArgsConstructor;
import org.beaconfire.composite.dto.FacilityReportDTO;
import org.beaconfire.composite.dto.PageListResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityReportService {
    public PageListResponse<FacilityReportDTO> getFacilityReportsForEmployee(int userId, int current, int pageSize) {
        // Placeholder response
        return new PageListResponse<>();
    }
}

