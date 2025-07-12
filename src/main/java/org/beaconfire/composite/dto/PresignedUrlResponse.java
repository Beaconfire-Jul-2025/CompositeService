package org.beaconfire.composite.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class PresignedUrlResponse {

    private String presignedUrl;
    private String key;
    private String bucketName;
    private Instant expiresAt;
    private String uploadInstructions;

    public PresignedUrlResponse(String presignedUrl, String key, String bucketName, Instant expiresAt) {
        this.presignedUrl = presignedUrl;
        this.key = key;
        this.bucketName = bucketName;
        this.expiresAt = expiresAt;
        this.uploadInstructions = "Use PUT request to upload file to the presigned URL";
    }
}