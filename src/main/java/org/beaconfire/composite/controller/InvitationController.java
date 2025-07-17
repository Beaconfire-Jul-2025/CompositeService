package org.beaconfire.composite.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beaconfire.composite.client.AuthenticationServiceClient;
import org.beaconfire.composite.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
@Slf4j
public class InvitationController {

    private final AuthenticationServiceClient authenticationServiceClient;

    @PostMapping
    public InvitationResponse sendInvitation(
            @Valid @RequestBody InvitationRequest request) {

        log.info("Sending invitation to email: {}", request.getEmail());

        try {
            // Get username from Spring Security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Create token request for invitation
            AuthenticationServiceClient.TokenRequest tokenRequest =
                    new AuthenticationServiceClient.TokenRequest();
            tokenRequest.setEmail(request.getEmail());
            tokenRequest.setPurpose("invitation");
            tokenRequest.setUserId(username);

            // Call authentication service to generate token
            ResponseEntity<ApiResponse<AuthenticationServiceClient.TokenResponse>> tokenResponse =
                    authenticationServiceClient.generateToken(tokenRequest);

            if (tokenResponse.getStatusCode().is2xxSuccessful() &&
                    tokenResponse.getBody() != null &&
                    tokenResponse.getBody().getData() != null) {

                AuthenticationServiceClient.TokenResponse tokenData = tokenResponse.getBody().getData();

                // Create response with token information
                InvitationResponse invitationResponse = new InvitationResponse();
                invitationResponse.setEmail(request.getEmail());
                invitationResponse.setToken(tokenData.getToken());
                invitationResponse.setMessage("Invitation sent successfully");
                invitationResponse.setExpiresIn(tokenData.getExpiresIn());

                log.info("Invitation sent successfully for email: {}", request.getEmail());

                return invitationResponse;

            } else {
                log.error("Failed to generate token for invitation: {}", request.getEmail());

                InvitationResponse invitationResponse = new InvitationResponse();
                invitationResponse.setEmail(request.getEmail());
                invitationResponse.setToken(null);
                invitationResponse.setMessage("Failed to generate invitation token");
                invitationResponse.setExpiresIn(0);
                return invitationResponse;
            }

        } catch (Exception e) {
            log.error("Error sending invitation for email: {}", request.getEmail(), e);

            InvitationResponse invitationResponse = new InvitationResponse();
            invitationResponse.setEmail(request.getEmail());
            invitationResponse.setToken(null);
            invitationResponse.setMessage("Internal server error while sending invitation");
            invitationResponse.setExpiresIn(0);
            return invitationResponse;
        }
    }

    @Data
    public static class InvitationRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
    }

    @Data
    public static class InvitationResponse {
        private String email;
        private String token;
        private String message;
        private long expiresIn;
    }
}
