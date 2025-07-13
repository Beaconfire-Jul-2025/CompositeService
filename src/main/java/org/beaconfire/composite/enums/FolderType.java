package org.beaconfire.composite.enums;

import lombok.Getter;

@Getter
public enum FolderType {
    TEMP("temp", "Temporary files", 24 * 60, 500), // 24 hours, 500MB
    AVATAR("avatar", "Avatar profile images", 7 * 24 * 60, 10), // 7 days, 10MB
    DRIVER_LICENSE("driver-license", "Driver license documents", 30 * 24 * 60, 50), // 30 days, 50MB
    VISA_DOCUMENTS("visa-documents", "Visa documents", 90 * 24 * 60, 100), // 90 days, 100MB
    PERSONAL_DOCUMENTS("personal-documents", "Personal documents", 365 * 24 * 60, 200); // 365 days, 200MB

    private final String folderName;
    private final String description;
    private final long expirationMinutes;
    private final long maxFileSizeMb;

    FolderType(String folderName, String description, long expirationMinutes, long maxFileSizeMb) {
        this.folderName = folderName;
        this.description = description;
        this.expirationMinutes = expirationMinutes;
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public static FolderType fromString(String folderType) {
        for (FolderType type : FolderType.values()) {
            if (type.name().equalsIgnoreCase(folderType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid folder type: " + folderType);
    }
}