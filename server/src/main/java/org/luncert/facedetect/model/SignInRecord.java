package org.luncert.facedetect.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@Entity
public class SignInRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "studentID", referencedColumnName = "id")
  private Student student;

  private Boolean beLate = false;

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "leaveSlipID", referencedColumnName = "id")
  private LeaveApplication leaveSlip;
}
