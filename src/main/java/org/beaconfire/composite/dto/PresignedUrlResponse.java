package org.beaconfire.composite.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PresignedUrlResponse {

    private String presignedUrl;
    private String key;
    private Instant expiresAt;

    public PresignedUrlResponse(String presignedUrl, String key, Instant expiresAt) {
        this.presignedUrl = presignedUrl;
        this.key = key;
        this.expiresAt = expiresAt;
    }
}