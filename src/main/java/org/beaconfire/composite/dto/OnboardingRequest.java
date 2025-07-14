package org.beaconfire.composite.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import java.time.Instant;
import java.util.List;

@Data
public class OnboardingRequest {

    @NotBlank
    private String ID;

    @NotBlank
    private String UserID;

    @NotBlank
    private String FirstName;

    @NotBlank
    private String LastName;

    private String MiddleName;
    private String PreferredName;
    private String AvatarPath;

    @Email
    @NotBlank
    private String Email;

    private String CellPhone;
    private String WorkPhone;
    private String Gender;
    private String SSN;
    private Instant DOB;
    private Instant StartDate;
    private Instant EndDate;
    private String HouseID;

    @Valid
    private List<Address> Addresses;

    @Valid
    @NotNull
    private WorkAuthorization WorkAuthorization;

    @Valid
    private DriverLicense DriverLicense;

    @Valid
    private List<EmergencyContact> EmergencyContacts;

    @Valid
    private List<Reference> References;

    @Valid
    private List<PersonalDocument> PersonalDocuments;

    @NotBlank
    private String ApplicationType;

    @Data
    public static class Address {
        @NotBlank
        private String Type;
        @NotBlank
        private String AddressLine1;
        private String AddressLine2;
        @NotBlank
        private String City;
        @NotBlank
        private String State;
        @NotBlank
        private String ZipCode;
    }

    @Data
    public static class WorkAuthorization {
        private Boolean IsUSCitizen;
        private Boolean GreenCardHolder;
        private String Type;
        private Instant StartDate;
        private Instant EndDate;
        private Instant LastModificationDate;
    }

    @Data
    public static class DriverLicense {
        private Boolean HasLicense;
        private String LicenseNumber;
        private Instant ExpirationDate;
    }

    @Data
    public static class EmergencyContact {
        @NotBlank
        private String FirstName;
        @NotBlank
        private String LastName;
        private String MiddleName;
        private String CellPhone;
        private String AlternatePhone;
        private String Email;
        @NotBlank
        private String Relationship;
        @Valid
        private Address Address;
    }

    @Data
    public static class Reference {
        @NotBlank
        private String FirstName;
        @NotBlank
        private String LastName;
        private String MiddleName;
        private String Phone;
        private String Email;
        @NotBlank
        private String Relationship;
        @Valid
        private Address Address;
    }

    @Data
    public static class PersonalDocument {
        @NotBlank
        private String Type;
        @NotBlank
        private String Path;
        private String Title;
        private String Comment;
        private Instant CreateDate;
    }
}
