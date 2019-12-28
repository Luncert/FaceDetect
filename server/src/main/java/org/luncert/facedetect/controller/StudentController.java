package org.luncert.facedetect.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.luncert.facedetect.dto.*;
import org.luncert.facedetect.exception.InvalidRequestParamException;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/student")
@PreAuthorize("hasRole('Student')")
public class StudentController {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private AdvanceSignInRecordRepo advanceSignInRecordRepo;

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    @Autowired
    private AdvanceLeaveSlipRepo advanceLeaveSlipRepo;

    @GetMapping("/profile")
    public StudentProfileDto getProfile(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        Student s = studentRepo.findById(sid).orElseThrow(IllegalAccessError::new);
        return new StudentProfileDto(s.getName());
    }

    @PostMapping("/faceData")
    public void updateFaceData(Authentication authentication,
                               @RequestParam("faceData") MultipartFile faceData) throws IOException {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        Student s = studentRepo.findById(sid).orElseThrow(IllegalArgumentException::new);
        s.setFaceData(faceData.getBytes());
        studentRepo.save(s);
    }

    @GetMapping("/courses")
    public List<StudentGetCourseDto> getCourses(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        List<Course> courseList = courseRepo.findByStudentID(sid);
        return courseList.stream()
                .map(c -> new StudentGetCourseDto(c.getId(), c.getName(), c.getTeacher().getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/course:{courseID}/signInRecords")
    public List<CourseSignInRecordDto> getCourseSignInRecords(Authentication authentication,
                                                              @PathVariable("courseID") long cid) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        return advanceSignInRecordRepo.findByStudentIdAndCourseId(sid, cid);
    }

    @PostMapping("/course:{courseID}/leaveSlip")
    public ResponseEntity applyForLeave(Authentication authentication,
                              @PathVariable("courseID") Long courseID,
                              @RequestParam("date") String date,
                              @RequestParam("content") String content,
                              @RequestParam(value = "attachment", required = false) MultipartFile attachment) throws IOException {
        if (!courseRepo.existsById(courseID)) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();

        LeaveSlip la = new LeaveSlip();
        la.setStudentID(sid);
        la.setCourseID(courseID);
        la.setState(LeaveSlip.State.UnProcessed);
        la.setCreateTime(System.currentTimeMillis());
        la.setDate(date);
        la.setContent(content);
        if (attachment != null) {
            la.setAttachment(new Attachment(attachment.getOriginalFilename(), attachment.getBytes()));
        }

        leaveSlipRepo.save(la);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/leaveSlips")
    public List<StudentGetLeaveSlipDto> getLeaveSlipList(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        return advanceLeaveSlipRepo.findByStudentID(sid);
    }
}
