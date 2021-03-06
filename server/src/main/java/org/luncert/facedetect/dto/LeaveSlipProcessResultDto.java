package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveSlipProcessResultDto {

    private boolean approved;

    private long signInID;

    private String comment;
}
