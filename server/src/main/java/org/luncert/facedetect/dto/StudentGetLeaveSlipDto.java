package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luncert.facedetect.model.LeaveSlip;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentGetLeaveSlipDto {

    private String courseName;

    private LeaveSlip.State state;

    private Long createTime;

    private String date;

    private String content;

    private String attachmentUrl;
}
