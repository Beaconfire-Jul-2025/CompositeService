package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.PageListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "housing-service", url = "${services.housing.url}")
public interface HousingServiceClient {

    @PostMapping("/api/housing")
    ResponseEntity<ApiResponse<HousingCreationResponse>> createHousing(@RequestBody HousingRequest request);

    @GetMapping("/api/housing")
    ResponseEntity<ApiResponse<PageListResponse<Housing>>> getAllHousing(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize);

    @GetMapping("/api/housing/{id}")
    ResponseEntity<ApiResponse<Housing>> getHousingById(@PathVariable String id);

    @PutMapping("/api/housing/{id}")
    ResponseEntity<ApiResponse<Housing>> updateHousing(@PathVariable String id, @RequestBody HousingRequest request);

    @DeleteMapping("/api/housing/{id}")
    ResponseEntity<ApiResponse<String>> deleteHousing(@PathVariable String id);

    @Data
    class HousingRequest {
        private String address;
        private String landlordName;
        private String landlordPhone;
        private String landlordEmail;
        private int maxOccupancy;
        private double rent;
        private String description;
    }

    @Data
    class HousingCreationResponse {
        private String housingId;
        private String message;
    }

    @Data
    class Housing {
        private String housingId;
        private String address;
        private String landlordName;
        private String landlordPhone;
        private String landlordEmail;
        private int maxOccupancy;
        private int currentOccupancy;
        private double rent;
        private String description;
        private String status;
        private String createdAt;
        private String updatedAt;
    }
}
