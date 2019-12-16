package org.luncert.facedetect.controller;

import org.luncert.facedetect.dto.LeaveSlipProcessResultDto;
import org.luncert.facedetect.dto.TeacherGetLeaveSlipDto;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/teacher")
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
        LeaveSlip leaveSlip = leaveSlipRepo.findById(leaveSlipID).orElseThrow();
        leaveSlip.setTeacherComment(result.getComment());
        if (result.isApproval()) {
            SignIn signIn = signInRepo.findById(result.getSignInID()).orElseThrow();
            Student student = studentRepo.findById(leaveSlip.getStudentID()).orElseThrow();

            leaveSlip.setState(LeaveSlip.State.Approved);
            // bind leave slip to SignInRecord
            SignInRecord signInRecord = new SignInRecord();
            signInRecord.setStudent(student);
            signInRecord.setLeaveSlip(leaveSlip);
            signInRecordRepo.save(signInRecord);

            signIn.getSignInRecords().add(signInRecord);
            signInRepo.save(signIn);
        } else {
            leaveSlip.setState(LeaveSlip.State.Rejected);
        }
        leaveSlipRepo.save(leaveSlip);
    }
}
