package org.beaconfire.composite.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class FacilityReportDTO {
    private Long id;
    private String title;
    private String description;
    private String createdBy;
    private ZonedDateTime reportDate;
    private String status;
    private List<CommentDTO> comments;

    @Data
    public static class CommentDTO {
        private Long commentId;
        private String description;
        private String createdBy;
        private ZonedDateTime commentDate;
    }
}

