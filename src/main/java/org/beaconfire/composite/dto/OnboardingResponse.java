package org.beaconfire.composite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponse {
    private String message;
    private String submissionId;
    private String status;

    public static OnboardingResponse success(String submissionId) {
        return new OnboardingResponse("Onboarding form submitted successfully", submissionId, "SUBMITTED");
    }

    public static OnboardingResponse validationFailed(String message) {
        return new OnboardingResponse(message, null, "VALIDATION_FAILED");
    }
}
