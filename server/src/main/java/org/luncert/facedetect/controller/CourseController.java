package org.luncert.facedetect.controller;

import com.google.common.collect.Lists;
import org.luncert.facedetect.dto.CreateCourseDto;
import org.luncert.facedetect.dto.UpdateCourseDto;
import org.luncert.facedetect.model.Course;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.Teacher;
import org.luncert.facedetect.model.UserAccount;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.StudentRepo;
import org.luncert.facedetect.repo.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user/teacher")
@PreAuthorize("hasRole('Teacher')")
public class CourseController {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @PostMapping("/course")
    public void createCourse(Authentication authentication,
                             @RequestBody CreateCourseDto createCourseDto) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        long teacherID = Long.valueOf(account.getObjectID());
        Teacher teacher = teacherRepo.findById(teacherID).get();

        Course course = new Course();
        course.setName(createCourseDto.getName());
        List<Student> studentList = Lists.newArrayList(studentRepo.findAllById(createCourseDto.getStudentIDList()));
        course.setStudent(studentList);
        course.setTeacher(teacher);
        courseRepo.save(course);
    }

    @GetMapping("/courses")
    public List<Course> getCourses(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        long teacherID = Long.valueOf(account.getObjectID());
        return courseRepo.findByTeacherID(teacherID);
    }

    @PutMapping("/{courseID}")
    public ResponseEntity updateCourse(@PathVariable("courseID") long cid,
                             @RequestBody UpdateCourseDto updateCourseDto) {
        String newName = updateCourseDto.getName();
        if (newName == null || newName.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid course name.");
        }
        Optional<Course> optionalCourse = courseRepo.findById(cid);
        if (!optionalCourse.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid course id.");
        }
        Course course = optionalCourse.get();
        course.setName(newName);
        courseRepo.save(course);
        return new ResponseEntity(HttpStatus.OK);
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
