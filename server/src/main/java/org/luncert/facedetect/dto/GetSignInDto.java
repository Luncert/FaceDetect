package org.luncert.facedetect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.luncert.facedetect.model.Student;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSignInDto {

    private Long id, startTime, endTime;

    private List<BasicStudentDto> lateStudentList, nonSignedInStudentList;
}
