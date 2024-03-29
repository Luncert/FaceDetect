package org.luncert.facedetect.controller;

import org.luncert.facedetect.dto.LeaveSlipProcessResultDto;
import org.luncert.facedetect.dto.TeacherGetLeaveSlipDto;
import org.luncert.facedetect.dto.TeacherGetStudentDto;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/teacher")
@PreAuthorize("hasRole('Teacher')")
public class TeacherController {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private SignInRepo signInRepo;

    @Autowired
    private SignInRecordRepo signInRecordRepo;

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    @Autowired
    private AdvanceLeaveSlipRepo advanceLeaveSlipRepo;

    @GetMapping("/leaveSlips")
    public List<TeacherGetLeaveSlipDto> getLeaveSlipList(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        long tid = Long.parseLong(account.getObjectID());
        return advanceLeaveSlipRepo.findByTeacherID(tid);
    }

    @PutMapping("/leaveSlip:{leaveSlipID}")
    public void processLeaveSlip(@PathVariable("leaveSlipID") Long leaveSlipID,
                              @RequestBody LeaveSlipProcessResultDto result) {
        LeaveSlip leaveSlip = leaveSlipRepo.findById(leaveSlipID).orElseThrow(IllegalArgumentException::new);
        leaveSlip.setTeacherComment(result.getComment());
        if (result.isApproved()) {
            SignIn signIn = signInRepo.findById(result.getSignInID()).orElseThrow(IllegalArgumentException::new);
            Student student = studentRepo.findById(leaveSlip.getStudentID()).orElseThrow(IllegalArgumentException::new);

            leaveSlip.setState(LeaveSlip.State.Approved);
            // bind leave slip to SignInRecord
            SignInRecord signInRecord = new SignInRecord();
            signInRecord.setSignIn(signIn);
            signInRecord.setStudent(student);
            signInRecord.setLeaveSlip(leaveSlip);
            signInRecordRepo.save(signInRecord);
        } else {
            leaveSlip.setState(LeaveSlip.State.Rejected);
        }
        leaveSlipRepo.save(leaveSlip);
    }

    // TODO: refactor this route
    @GetMapping("/students")
    public List<TeacherGetStudentDto> getStudents() {
        List<TeacherGetStudentDto> ret = new ArrayList<>();
        for (Student student : studentRepo.findAll()) {
            ret.add(new TeacherGetStudentDto(student.getId(), student.getName()));
        }
        return ret;
    }
}
