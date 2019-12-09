package org.luncert.facedetect.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user/teacher")
@PreAuthorize("hasRole('Teacher')")
public class CourseController {

    @PostMapping("/course")
    public void createCourse(@RequestBody Object courseInfo) {

    }

    @GetMapping("/courses")
    public List getCourses() {
        return null;
    }

    @PutMapping("/{courseID}")
    public void updateCourse(@PathVariable("courseID") long cid,
                             @RequestBody Objects courseInfo) {

    }

    @PostMapping("/{courseID}/{studentID}")
    public void addStudentToCourse(@PathVariable("courseID") long cid,
                                   @PathVariable("studentID") String sid) {

    }

    @DeleteMapping("/{courseID}/{studentID}")
    public void removeStudentFromCourse(@PathVariable("courseID") long cid,
                                        @PathVariable("studentID") String sid) {

    }

    @DeleteMapping("/{courseID}")
    public void removeCourse(@PathVariable("courseID") long cid) {

    }
}
