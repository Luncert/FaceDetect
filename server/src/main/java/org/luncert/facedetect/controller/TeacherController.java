package org.luncert.facedetect.controller;

import org.luncert.facedetect.dto.LeaveApplyPostResultDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/teacher")
public class TeacherController {

    @GetMapping("/leaveApplications")
    public List getLeaveApplications() {
        return null;
    }

    @GetMapping("/{leaveApplicationID}")
    public void postLeaveApplication(@PathVariable("leaveApplicationID") String laID,
                                     @RequestBody LeaveApplyPostResultDto result) {

    }
}
