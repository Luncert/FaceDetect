package org.luncert.facedetect.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class LeaveApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private State state;

  private Long createTime;

  private String date;

  private String content;

  private byte[] attachment;

  public enum State {
    UnProcessed,
    Approved,
    Rejected;
  }
}
