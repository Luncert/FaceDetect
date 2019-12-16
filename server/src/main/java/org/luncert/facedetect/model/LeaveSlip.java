package org.luncert.facedetect.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class LeaveSlip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String studentID;

  private Long courseID;

  private State state;

  private Long createTime;

  private String date;

  @Column(columnDefinition = "Blob")
  private String content;

  @Embedded
  private Attachment attachment;

  public enum State {
    UnProcessed,
    Approved,
    Rejected;
  }

  private String teacherComment;
}
