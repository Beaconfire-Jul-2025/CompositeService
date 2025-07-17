package org.beaconfire.composite.client;

import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.PageListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "email-service", url = "${services.email.url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/send")
    ResponseEntity<ApiResponse<EmailSendResponse>> sendEmail(@RequestBody EmailRequest request);

    @GetMapping("/api/email/templates")
    ResponseEntity<ApiResponse<PageListResponse<EmailTemplate>>> getEmailTemplates(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize);

    @GetMapping("/api/email/history")
    ResponseEntity<ApiResponse<PageListResponse<EmailHistory>>> getEmailHistory(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize);

    @Data
    class EmailRequest {
        private String to;
        private String subject;
        private String body;
        private String templateId;
    }

    @Data
    class EmailSendResponse {
        private String emailId;
        private String status;
        private String message;
    }

    @Data
    class EmailTemplate {
        private String templateId;
        private String name;
        private String subject;
        private String body;
    }

    @Data
    class EmailHistory {
        private String emailId;
        private String to;
        private String subject;
        private String status;
        private String sentAt;
    }
}
