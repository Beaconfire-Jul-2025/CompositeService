package org.beaconfire.composite.client;

import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.FacilityReportDTO;
import org.beaconfire.composite.dto.PageListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facility-report-service", url = "${services.facilityReport.url}")
public interface FacilityReportServiceClient {
    @GetMapping("/api/facility-report")
    ResponseEntity<ApiResponse<PageListResponse<FacilityReportDTO>>> getFacilityReportsByHouseIds(
            @RequestParam("houseIds") String houseIds,
            @RequestParam("current") int current,
            @RequestParam("pageSize") int pageSize);
}

