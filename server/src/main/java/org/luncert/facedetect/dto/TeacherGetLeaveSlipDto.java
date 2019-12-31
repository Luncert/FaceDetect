package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luncert.facedetect.model.LeaveSlip;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherGetLeaveSlipDto {

    private Long id;

    private Long courseID;

    private String courseName;

    private String studentID;

    private String studentName;

    private LeaveSlip.State state;

    private Long createTime;

    private String date;

    private String content;

    private String attachmentUrl;
}
