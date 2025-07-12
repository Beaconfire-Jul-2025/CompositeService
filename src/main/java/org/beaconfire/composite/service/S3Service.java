package org.beaconfire.composite.service;

import org.beaconfire.composite.dto.PresignedUrlRequest;
import org.beaconfire.composite.dto.PresignedUrlResponse;
import org.beaconfire.composite.enums.BucketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    // Allowed content types for each bucket type
    private static final List<String> AVATAR_ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> DOCUMENT_ALLOWED_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/png", "image/tiff", "image/bmp"
    );
    private final S3Presigner s3Presigner;
    @Value("${aws.s3.presigned-url.default-expiration-minutes:60}")
    private long defaultExpirationMinutes;

    public S3Service(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    // Modified method signature to directly accept BucketType
    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request, BucketType bucketType) {
        // BucketType bucketType = BucketType.fromString(request.getBucketType()); // This line is removed
        validateRequest(request, bucketType);

        String key = generateKey(request, bucketType);
        Duration expiration = Duration.ofMinutes(bucketType.getExpirationMinutes());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketType.getBucketName())
                .key(key)
                .contentType(request.getContentType())
                .contentLength(request.getFileSizeBytes())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        Instant expiresAt = Instant.now().plus(expiration);

        logger.info("Generated presigned URL for bucket: {}, key: {}, expires at: {}",
                bucketType.getBucketName(), key, expiresAt);

        return new PresignedUrlResponse(
                presignedRequest.url().toString(),
                key,
                bucketType.getBucketName(),
                expiresAt
        );
    }

    private void validateRequest(PresignedUrlRequest request, BucketType bucketType) {
        // File size validation
        long maxSizeBytes = bucketType.getMaxFileSizeMb() * 1024 * 1024;
        if (request.getFileSizeBytes() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB for bucket type %s",
                            bucketType.getMaxFileSizeMb(), bucketType.name()));
        }

        // Content type validation
        if (!isValidContentType(request.getContentType(), bucketType)) {
            throw new IllegalArgumentException(
                    String.format("Content type '%s' is not allowed for bucket type %s",
                            request.getContentType(), bucketType.name()));
        }
    }

    private boolean isValidContentType(String contentType, BucketType bucketType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return false;
        }

        switch (bucketType) {
            case AVATAR:
                return AVATAR_ALLOWED_TYPES.contains(contentType.toLowerCase());
            case DRIVER_LICENSE:
            case VISA_DOCUMENTS:
            case PERSONAL_DOCUMENTS:
                return DOCUMENT_ALLOWED_TYPES.contains(contentType.toLowerCase());
            case TEMP:
                return true; // Allow any content type for temp bucket
            default:
                return false;
        }
    }

    private String generateKey(PresignedUrlRequest request, BucketType bucketType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedFileName = sanitizeFileName(request.getFileName());

        StringBuilder keyBuilder = new StringBuilder();

        // Add bucket type prefix
        keyBuilder.append(bucketType.name().toLowerCase()).append("/");

        // Add optional folder
        if (request.getFolder() != null && !request.getFolder().trim().isEmpty()) {
            String sanitizedFolder = sanitizeFolder(request.getFolder());
            keyBuilder.append(sanitizedFolder).append("/");
        }

        // Add timestamp and unique ID for uniqueness
        keyBuilder.append(timestamp).append("_").append(uniqueId).append("_").append(sanitizedFileName);

        return keyBuilder.toString();
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String sanitizeFolder(String folder) {
        return folder.replaceAll("^/+|/+$", "")
                .replaceAll("[^a-zA-Z0-9._/-]", "_");
    }

    public List<BucketType> getAllBucketTypes() {
        return Arrays.asList(BucketType.values());
    }
}