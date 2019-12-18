package org.luncert.facedetect.controller;

import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.Teacher;
import org.luncert.facedetect.model.UserAccount;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.model.UserRole;
import org.luncert.facedetect.repo.StudentRepo;
import org.luncert.facedetect.repo.TeacherRepo;
import org.luncert.facedetect.repo.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserInfoController {

  @Autowired
  private StudentRepo studentRepo;

  @Autowired
  private TeacherRepo teacherRepo;

  @Autowired
  private UserInfoRepo userInfoRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/unauthorized")
  public ResponseEntity<String> unauthorizedPage() {
    return new ResponseEntity<>("Access denied.", HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/student")
  public void createStudent(String id, String name) {
    Student student = new Student();
    student.setId(id);
    student.setName(name);
    studentRepo.save(student);
    UserInfo userInfo = new UserInfo();
    userInfo.setAccount(new UserAccount(student.getId(), UserRole.Student));
    userInfo.setPassword(passwordEncoder.encode("123456"));
    userInfoRepo.save(userInfo);
  }

  @PostMapping("/teacher")
  public String createTeacher(String name) {
    Teacher teacher = new Teacher();
    teacherRepo.save(teacher);
    UserInfo userInfo = new UserInfo();
    userInfo.setAccount(new UserAccount(String.valueOf(teacher.getId()), UserRole.Teacher));
    userInfo.setPassword(passwordEncoder.encode("123456"));
    userInfoRepo.save(userInfo);
    return userInfo.getAccount().toString();
  }
}
