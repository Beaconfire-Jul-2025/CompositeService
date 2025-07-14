package org.beaconfire.composite.client;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "application-service", url = "${services.application.url}")
public interface ApplicationServiceClient {

    @PostMapping("/api/applications")
    ResponseEntity<ApplicationCreationResponse> createApplication(@RequestBody ApplicationRequest request);

    @Data
    class ApplicationRequest {
        private String employeeId;
        private String applicationType;
    }

    @Data
    class ApplicationCreationResponse {
        private String applicationId;
        private String message;
    }
}
