package org.beaconfire.composite.controller;

import org.beaconfire.composite.dto.OnboardingRequest;
import org.beaconfire.composite.dto.OnboardingResponse;
import org.beaconfire.composite.service.OnboardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingController.class);

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/submit")
    public ResponseEntity<OnboardingResponse> submitOnboarding(
            @Valid @RequestBody OnboardingRequest request) {

        logger.info("Received onboarding submission for employee ID: {}", request.getID());

        try {
            OnboardingResponse response = onboardingService.submitOnboarding(request);

            if ("VALIDATION_FAILED".equals(response.getStatus())) {
                logger.warn("Onboarding validation failed: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Onboarding submission successful with ID: {}", response.getSubmissionId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing onboarding submission", e);
            OnboardingResponse errorResponse = OnboardingResponse.validationFailed("Internal server error occurred");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Onboarding Service is healthy");
    }
}
