package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.OnboardingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service", url = "${services.employee.url}")
public interface EmployeeServiceClient {

    @PostMapping("/api/Employee/validate")
    ResponseEntity<ApiResponse<ValidationResponse>> validateOnboardingForm(@RequestBody OnboardingRequest request);

    @PostMapping("/api/Employee")
    ResponseEntity<ApiResponse<EmployeeCreationResponse>> createEmployee(@RequestBody OnboardingRequest request);

    @Data
    class ValidationResponse {
        private boolean valid;
        private String message;
    }

    @Data
    class EmployeeCreationResponse {
        private String employeeId;
        private String message;

    }
}
