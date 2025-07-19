package org.beaconfire.composite.client;

import lombok.Builder;
import lombok.Data;
import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.OnboardingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "employee-service", url = "${services.employee.url}")
public interface EmployeeServiceClient {

    @PostMapping("/api/Employee")
    ResponseEntity<ApiResponse<EmployeeCreationResponse>> createEmployee(@RequestBody OnboardingRequest request);

    @GetMapping("/api/employee/profile")
    ResponseEntity<ApiResponse<Employee>> getProfile(@RequestHeader Map<String, String> headers);

    @Data
    @Builder
    class Employee {
        private String id;
        private String userId;
        private String firstName;
        private String lastName;
        private String middleName;
        private String preferredName;
        private String avatarPath;
        private String email;
        private String cellPhone;
        private String alternatePhone;
        private String gender;
        private String ssn;
        private java.util.Date dob;
        private java.util.Date startDate;
        private java.util.Date endDate;
        private String houseId;
        private java.util.List<Address> addresses;
        private WorkAuthorization workAuthorization;
        private DriverLicense driverLicense;
        private java.util.List<EmergencyContact> emergencyContacts;
        private java.util.List<Reference> references;
        private java.util.List<PersonalDocument> personalDocuments;
        private String applicationType;
    }

    @Data
    @Builder
    class Address {
        private String id;
        private String type;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
    }

    @Data
    @Builder
    class DriverLicense {
        private Boolean hasLicense;
        private String licenseNumber;
        private java.util.Date expirationDate;
    }

    @Data
    @Builder
    class EmergencyContact {
        private String id;
        private String firstName;
        private String lastName;
        private String middleName;
        private String cellPhone;
        private String alternatePhone;
        private String email;
        private String relationship;
        private Address address;
    }

    @Data
    @Builder
    class PersonalDocument {
        private String id;
        private String type;
        private String path;
        private String title;
        private String comment;
        private java.util.Date createDate;
    }

    @Data
    @Builder
    class Reference {
        private String id;
        private String firstName;
        private String lastName;
        private String middleName;
        private String phone;
        private String email;
        private String relationship;
        private Address address;
    }

    @Data
    @Builder
    class WorkAuthorization {
        private Boolean isUsCitizen;
        private Boolean greenCardHolder;
        private String type;
        private java.util.Date startDate;
        private java.util.Date endDate;
        private java.util.Date lastModificationDate;
    }

    @Data
    class EmployeeCreationResponse {
        private String employeeId;
        private String message;

    }
}
