package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentGetCourseDto {

    private Long courseID;

    private String courseName;

    private String teacherName;
}
