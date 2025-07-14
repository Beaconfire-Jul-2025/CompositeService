package org.beaconfire.composite.client;

import lombok.Getter;
import lombok.Setter;
import org.beaconfire.composite.dto.OnboardingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service", url = "${services.employee.url}")
public interface EmployeeServiceClient {

    @PostMapping("/api/Employee/validate")
    ResponseEntity<ValidationResponse> validateOnboardingForm(@RequestBody OnboardingRequest request);

    @PostMapping("/api/Employee")
    ResponseEntity<EmployeeCreationResponse> createEmployee(@RequestBody OnboardingRequest request);

    class ValidationResponse {
        private boolean valid;
        private String message;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @Data
    class EmployeeCreationResponse {
        private String employeeId;
        private String message;

    }
}
