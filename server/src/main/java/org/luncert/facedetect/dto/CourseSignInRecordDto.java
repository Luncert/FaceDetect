package org.luncert.facedetect.dto;

import lombok.Data;

@Data
public class CourseSignInRecordDto {

    private Long startTime, endTime, recordTime;

    private Boolean beLate;

    private Boolean hasLeaveSlip;
}
