package org.luncert.facedetect.model;

import lombok.Data;

import javax.persistence.*;

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

  @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
  @JoinColumn(name = "leaveSlipID", referencedColumnName = "id")
  private LeaveApplication leaveSlip;
}
