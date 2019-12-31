package org.luncert.facedetect.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class SignInRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long createTime;

  @ManyToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "sign_in_id", referencedColumnName = "id")
  private SignIn signIn;

  @OneToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "student_id", referencedColumnName = "id")
  private Student student;

  @Column(columnDefinition = "tinyint(1)")
  private Boolean beLate;

  @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
  @JoinColumn(name = "leaveSlipID", referencedColumnName = "id")
  private LeaveSlip leaveSlip;
}
