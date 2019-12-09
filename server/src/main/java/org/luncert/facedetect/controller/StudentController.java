package org.luncert.facedetect.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/student")
public class StudentController {

    @GetMapping("/profile")
    public Object getProfile() {
        return null;
    }

    @PostMapping("/faceData")
    public void saveFaceData(@RequestBody byte[] faceData) {

    }

    @GetMapping("/courses")
    public List getCourses() {
        return null;
    }

    @GetMapping("/{courseID}/signInRecords")
    public List getCourseSignInRecords(@PathVariable("courseID") long cid) {
        return null;
    }

    @PostMapping("/leaveApplication")
    public void applyForLeave(@RequestBody Object applyInfo) {

    }

    @GetMapping("/leaveApplications")
    public List getLeaveApplications() {
        return null;
    }
}
