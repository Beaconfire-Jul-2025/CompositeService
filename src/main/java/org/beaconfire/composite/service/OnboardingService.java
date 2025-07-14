package org.beaconfire.composite.service;

import org.beaconfire.composite.client.ApplicationServiceClient;
import org.beaconfire.composite.client.EmployeeServiceClient;
import org.beaconfire.composite.configuration.RabbitMQConfig;
import org.beaconfire.composite.dto.OnboardingRequest;
import org.beaconfire.composite.dto.OnboardingResponse;
import org.beaconfire.composite.enums.FolderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.UUID;

@Service
public class OnboardingService {

    private static final Logger logger = LoggerFactory.getLogger(OnboardingService.class);

    private final EmployeeServiceClient employeeServiceClient;
    private final ApplicationServiceClient applicationServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public OnboardingService(EmployeeServiceClient employeeServiceClient,
                           ApplicationServiceClient applicationServiceClient,
                           RabbitTemplate rabbitTemplate,
                           S3Client s3Client) {
        this.employeeServiceClient = employeeServiceClient;
        this.applicationServiceClient = applicationServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.s3Client = s3Client;
    }

    public OnboardingResponse submitOnboarding(OnboardingRequest request) {
        logger.info("Starting onboarding submission for employee ID: {}", request.getID());

        try {
            // Validate form with Employee service
            EmployeeServiceClient.ValidationResponse validationResponse =
                employeeServiceClient.validateOnboardingForm(request).getBody();

            if (validationResponse == null || !validationResponse.isValid()) {
                String message = validationResponse != null ? validationResponse.getMessage() : "Validation failed";
                logger.warn("Onboarding validation failed: {}", message);
                return OnboardingResponse.validationFailed(message);
            }

            // Generate submission ID
            String submissionId = UUID.randomUUID().toString();
            logger.info("Validation passed, sending to queue with submission ID: {}", submissionId);

            // Add submission ID to request for tracking
            OnboardingQueueMessage queueMessage = new OnboardingQueueMessage(submissionId, request);

            // Send to RabbitMQ queue for processing
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ONBOARDING_EXCHANGE,
                RabbitMQConfig.ONBOARDING_ROUTING_KEY,
                queueMessage
            );

            return OnboardingResponse.success(submissionId);

        } catch (Exception e) {
            logger.error("Error during onboarding submission", e);
            return OnboardingResponse.validationFailed("Internal error occurred during submission");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ONBOARDING_QUEUE)
    public void processOnboarding(OnboardingQueueMessage message) {
        logger.info("Processing onboarding for submission ID: {}", message.getSubmissionId());

        try {
            OnboardingRequest request = message.getRequest();

            // Move files from temp to permanent locations
            moveFilesToPermanentLocations(request);

            // Create employee record
            EmployeeServiceClient.EmployeeCreationResponse employeeResponse =
                employeeServiceClient.createEmployee(request).getBody();

            if (employeeResponse == null || employeeResponse.getEmployeeId() == null) {
                logger.error("Failed to create employee for submission: {}", message.getSubmissionId());
                return;
            }

            String employeeId = employeeResponse.getEmployeeId();
            logger.info("Employee created with ID: {} for submission: {}", employeeId, message.getSubmissionId());

            // Create application record
            ApplicationServiceClient.ApplicationRequest appRequest =
                new ApplicationServiceClient.ApplicationRequest(employeeId, request.getApplicationType());

            ApplicationServiceClient.ApplicationCreationResponse appResponse =
                applicationServiceClient.createApplication(appRequest).getBody();

            if (appResponse != null && appResponse.getApplicationId() != null) {
                logger.info("Application created with ID: {} for employee: {}",
                    appResponse.getApplicationId(), employeeId);
            } else {
                logger.warn("Failed to create application for employee: {}", employeeId);
            }

            logger.info("Onboarding processing completed for submission: {}", message.getSubmissionId());

        } catch (Exception e) {
            logger.error("Error processing onboarding for submission: {}", message.getSubmissionId(), e);
        }
    }

    private void moveFilesToPermanentLocations(OnboardingRequest request) {
        logger.info("Moving files from temp to permanent locations for employee: {}", request.getID());

        try {
            // Move avatar file
            if (request.getAvatarPath() != null && !request.getAvatarPath().isEmpty()) {
                String newAvatarPath = moveFile(request.getAvatarPath(), FolderType.AVATAR);
                request.setAvatarPath(newAvatarPath);
                logger.info("Moved avatar file to: {}", newAvatarPath);
            }

            // Move personal documents
            if (request.getPersonalDocuments() != null) {
                for (OnboardingRequest.PersonalDocument doc : request.getPersonalDocuments()) {
                    FolderType targetFolder = determineFolderType(doc.getType());
                    String newPath = moveFile(doc.getPath(), targetFolder);
                    doc.setPath(newPath);
                    logger.info("Moved document {} to: {}", doc.getType(), newPath);
                }
            }

        } catch (Exception e) {
            logger.error("Error moving files for employee: {}", request.getID(), e);
            throw new RuntimeException("Failed to move files to permanent locations", e);
        }
    }

    private FolderType determineFolderType(String documentType) {
        switch (documentType.toUpperCase()) {
            case "DRIVER_LICENSE_PROOF":
                return FolderType.DRIVER_LICENSE;
            case "WORK_AUTHORIZATION_PROOF":
            case "VISA_PROOF":
                return FolderType.VISA_DOCUMENTS;
            default:
                return FolderType.PERSONAL_DOCUMENTS;
        }
    }

    private String moveFile(String currentPath, FolderType targetFolder) {
        try {
            // Extract file name from current path
            String fileName = currentPath.substring(currentPath.lastIndexOf("/") + 1);
            String newKey = targetFolder.getFolderName() + "/" + fileName;

            // Copy object to new location
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(currentPath.startsWith("/") ? currentPath.substring(1) : currentPath)
                .destinationBucket(bucketName)
                .destinationKey(newKey)
                .build();

            s3Client.copyObject(copyRequest);

            // Delete original file
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(currentPath.startsWith("/") ? currentPath.substring(1) : currentPath)
                .build();

            s3Client.deleteObject(deleteRequest);

            return "/" + newKey;

        } catch (Exception e) {
            logger.error("Failed to move file from {} to {}", currentPath, targetFolder.getFolderName(), e);
            throw new RuntimeException("File move operation failed", e);
        }
    }

    public static class OnboardingQueueMessage {
        private String submissionId;
        private OnboardingRequest request;

        public OnboardingQueueMessage() {}

        public OnboardingQueueMessage(String submissionId, OnboardingRequest request) {
            this.submissionId = submissionId;
            this.request = request;
        }

        public String getSubmissionId() { return submissionId; }
        public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
        public OnboardingRequest getRequest() { return request; }
        public void setRequest(OnboardingRequest request) { this.request = request; }
    }
}
