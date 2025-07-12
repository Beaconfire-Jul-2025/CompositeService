package org.beaconfire.composite.enums;

import lombok.Getter;

@Getter
public enum BucketType {
    TEMP("temp-bucket", "Temporary files", 24 * 60, 500), // 24 hours, 500MB
    AVATAR("avatar-bucket", "Avatar profile images", 7 * 24 * 60, 10), // 7 days, 10MB
    DRIVER_LICENSE("driver-license-bucket", "Driver license documents", 30 * 24 * 60, 50), // 30 days, 50MB
    VISA_DOCUMENTS("visa-documents-bucket", "Visa documents", 90 * 24 * 60, 100), // 90 days, 100MB
    PERSONAL_DOCUMENTS("personal-documents-bucket", "Personal documents", 365 * 24 * 60, 200); // 365 days, 200MB

    private final String bucketName;
    private final String description;
    private final long expirationMinutes;
    private final long maxFileSizeMb;

    BucketType(String bucketName, String description, long expirationMinutes, long maxFileSizeMb) {
        this.bucketName = bucketName;
        this.description = description;
        this.expirationMinutes = expirationMinutes;
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public static BucketType fromString(String bucketType) {
        for (BucketType type : BucketType.values()) {
            if (type.name().equalsIgnoreCase(bucketType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid bucket type: " + bucketType);
    }
}