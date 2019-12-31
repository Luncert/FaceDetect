package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentGetCourseDto {

    private Long courseID;

    private String courseName;

    private String teacherName;

    private Integer lateTimes;

    private Integer nonSignedInTimes;

    private List<CourseSignInDetailDto> signInDetails;
}
