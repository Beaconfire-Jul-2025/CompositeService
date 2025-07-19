package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service", url = "${services.email.url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/send")
    ResponseEntity<ApiResponse<EmailSendResponse>> sendEmail(@RequestBody EmailRequest request);

    @Data
    class EmailRequest {
        private String to;
        private String subject;
        private String body;
    }

    @Data
    class EmailSendResponse {
        private String emailId;
        private String status;
        private String message;
    }
}
