package org.beaconfire.composite.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beaconfire.composite.client.AuthenticationServiceClient;
import org.beaconfire.composite.client.EmailServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
@Slf4j
public class InvitationController {

    private final AuthenticationServiceClient authenticationServiceClient;
    private final EmailServiceClient emailServiceClient;

    @PostMapping
    public InvitationResponse sendInvitation(@RequestBody InvitationRequest request) {
        log.info("Received invitation request for email: {}", request.getEmail());
        try {
            // Call auth service to get token
            AuthenticationServiceClient.TokenRequest tokenRequest = new AuthenticationServiceClient.TokenRequest();
            tokenRequest.setEmail(request.getEmail());
            tokenRequest.setUserId(request.getUserId() == null ? null : request.getUserId().toString());
            tokenRequest.setPurpose("invitation");

            // Get token from auth service
            ResponseEntity<org.beaconfire.composite.dto.ApiResponse<AuthenticationServiceClient.TokenResponse>> tokenResponseEntity = authenticationServiceClient.generateToken(tokenRequest);
            if (tokenResponseEntity == null || !tokenResponseEntity.getStatusCode().is2xxSuccessful() || tokenResponseEntity.getBody() == null || tokenResponseEntity.getBody().getData() == null) {
                log.error("Failed to generate token for email: {}", request.getEmail());
                return new InvitationResponse(request.getEmail(), null, "Failed to generate token", null);
            }
            AuthenticationServiceClient.TokenResponse tokenData = tokenResponseEntity.getBody().getData();

            // Pass token and email to email service
            EmailServiceClient.EmailRequest emailRequest = new EmailServiceClient.EmailRequest();
            emailRequest.setTo(request.getEmail());
            emailRequest.setSubject("Invitation");
            emailRequest.setBody("Your invitation token: " + tokenData.getToken());

            ResponseEntity<org.beaconfire.composite.dto.ApiResponse<EmailServiceClient.EmailSendResponse>> emailResponseEntity = emailServiceClient.sendEmail(emailRequest);
            boolean emailSent = emailResponseEntity != null && emailResponseEntity.getStatusCode().is2xxSuccessful() && emailResponseEntity.getBody() != null && "SUCCESS".equalsIgnoreCase(emailResponseEntity.getBody().getData().getStatus());

            if (emailSent) {
                log.info("Invitation email sent to: {}", request.getEmail());
                return new InvitationResponse(request.getEmail(), tokenData.getToken(), "Invitation sent successfully", String.valueOf(tokenData.getExpiresIn()));
            } else {
                log.error("Failed to send invitation email to: {}", request.getEmail());
                return new InvitationResponse(request.getEmail(), tokenData.getToken(), "Failed to send invitation email", String.valueOf(tokenData.getExpiresIn()));
            }
        } catch (Exception e) {
            log.error("Error processing invitation for email: {}", request.getEmail(), e);
            return new InvitationResponse(request.getEmail(), null, "Internal server error", null);
        }
    }

    @Data
    public static class InvitationRequest {
        private String email;
        private Integer userId;
    }

    @Data
    public static class InvitationResponse {
        private String email;
        private String token;
        private String message;
        private String expiration;

        public InvitationResponse(String email, String token, String message, String expiration) {
            this.email = email;
            this.token = token;
            this.message = message;
            this.expiration = expiration;
        }
    }
}
