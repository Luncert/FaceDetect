package org.luncert.facedetect.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateCourseDto {

  private String name;

  private List<String> studentIDList;
}
