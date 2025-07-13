package org.beaconfire.composite.service;

import org.beaconfire.composite.dto.PresignedUrlRequest;
import org.beaconfire.composite.dto.PresignedUrlResponse;
import org.beaconfire.composite.enums.FolderType;
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
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private final S3Presigner s3Presigner;
    @Value("${aws.s3.presigned-url.default-expiration-minutes:60}")
    private long defaultExpirationMinutes;
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.endpoint:}")
    private String endpoint;

    public S3Service(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        validateRequest(request);

        String key = generateKey(request);
        // Always use TEMP folder type
        Duration expiration = Duration.ofMinutes(FolderType.TEMP.getExpirationMinutes());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
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

        String presignedUrl = presignedRequest.url().toString();
        // Only rewrite to path-style if endpoint is set (i.e., using LocalStack)
        if (endpoint != null && !endpoint.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(presignedUrl);
                String newHost = new java.net.URL(endpoint).getAuthority();
                String newPath = "/" + bucketName + url.getPath();
                presignedUrl = url.getProtocol() + "://" + newHost + newPath + (url.getQuery() != null ? ("?" + url.getQuery()) : "");
            } catch (Exception e) {
                logger.warn("Failed to rewrite presigned URL to path-style: {}", e.getMessage());
            }
        }

        logger.info("Generated presigned URL for key: {}, expires at: {}",
                key, expiresAt);

        return new PresignedUrlResponse(
                presignedUrl,
                key,
                expiresAt
        );
    }

    private void validateRequest(PresignedUrlRequest request) {
        long maxSizeBytes = FolderType.TEMP.getMaxFileSizeMb() * 1024 * 1024;
        if (request.getFileSizeBytes() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB for TEMP folder",
                            FolderType.TEMP.getMaxFileSizeMb()));
        }
    }

    private String generateKey(PresignedUrlRequest request) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedFileName = sanitizeFileName(request.getFileName());
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(FolderType.TEMP.getFolderName()).append("/");
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
}