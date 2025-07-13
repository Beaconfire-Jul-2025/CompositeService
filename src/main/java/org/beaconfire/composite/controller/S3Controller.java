package org.beaconfire.composite.controller;

import org.beaconfire.composite.dto.PresignedUrlRequest;
import org.beaconfire.composite.dto.PresignedUrlResponse;
import org.beaconfire.composite.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/s3")
@CrossOrigin(origins = "*")
public class S3Controller {

    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {

        logger.info("Received request for presigned URL: fileName={}, contentType={}, size={}",
                request.getFileName(), request.getContentType(), request.getFileSizeBytes());

        try {
            PresignedUrlResponse response = s3Service.generatePresignedUrl(request);
            logger.info("Successfully generated presigned URL for key: {}", response.getKey());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating presigned URL", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("S3 Presigned URL Service is healthy");
    }
}