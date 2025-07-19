package org.beaconfire.composite.dto;

import lombok.Data;

@Data
public class PresignedUrlRequest {

    private String filePurpose;

    private String fileType;
}