package org.luncert.facedetect.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateCourseDto {

  private String name;

  private Set<String> studentIDList;
}
