package org.luncert.facedetect.dto;

import lombok.Data;

@Data
public class LeaveApplyPostResultDto {

    private boolean approval;

    private long signInID;

    private String comment;
}
