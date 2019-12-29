package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSignInDetailDto {

  private long startTime, endTime;

  private boolean beLate;

  private boolean nonSignedIn;
}
