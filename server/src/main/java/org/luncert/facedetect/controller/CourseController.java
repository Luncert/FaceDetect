package org.luncert.facedetect.controller;

import com.google.common.collect.Sets;
import org.luncert.facedetect.dto.BasicCourseDto;
import org.luncert.facedetect.dto.BasicStudentDto;
import org.luncert.facedetect.dto.CreateCourseDto;
import org.luncert.facedetect.dto.UpdateCourseDto;
import org.luncert.facedetect.exception.InvalidRequestParamException;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        long teacherID = Long.parseLong(account.getObjectID());
        Teacher teacher = teacherRepo.findById(teacherID).get();
        // create course and bind teacher, students
        Course course = new Course();
        course.setName(createCourseDto.getName());
        course.setTeacher(teacher);
        Set<Student> students = Sets.newLinkedHashSet(studentRepo.findAllById(createCourseDto.getStudentIDList()));
        course.setStudent(students);
        courseRepo.save(course);
    }

    /**
     * find all of the teacher's courses
     * @param authentication
     * @return course list
     */
    @GetMapping("/courses")
    public List<BasicCourseDto> getCourses(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        long teacherID = Long.parseLong(account.getObjectID());
        List<Course> courseList = courseRepo.findByTeacherID(teacherID);
        return courseList.stream()
                .map(course -> new BasicCourseDto(course.getId(), course.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/course:{cid}/students")
    public ResponseEntity getCourseStudents(@PathVariable("cid") long cid) {
        Optional<Course> optionalCourse = courseRepo.findById(cid);
        if (!optionalCourse.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid course id.");
        }
        List<BasicStudentDto> studentList = optionalCourse.get().getStudent().stream()
                .map(s -> new BasicStudentDto(s.getId(), s.getName(), s.getFaceData() == null))
                .collect(Collectors.toList());
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @PutMapping("/course:{cid}")
    public ResponseEntity updateCourse(@PathVariable("cid") long cid,
                             @RequestBody UpdateCourseDto updateCourseDto) {
        String newName = updateCourseDto.getName();
        if (newName == null || newName.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid course name.");
        }
        Optional<Course> optionalCourse = courseRepo.findById(cid);
        if (!optionalCourse.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid course id.");
        }
        // update course name
        Course course = optionalCourse.get();
        course.setName(newName);
        courseRepo.save(course);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/course:{cid}/student:{sid}")
    public ResponseEntity addStudentToCourse(@PathVariable("cid") long cid,
                                   @PathVariable("sid") String sid) {
        Optional<Course> optionalCourse = courseRepo.findById(cid);
        if (!optionalCourse.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid course id.");
        }
        Course course = optionalCourse.get();
        Optional<Student> optionalStudent = studentRepo.findById(sid);
        if (!optionalStudent.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid student id.");
        }
        Student student = optionalStudent.get();
        course.getStudent().add(student);
        courseRepo.save(course);
        return new ResponseEntity(HttpStatus.OK);
    }

    // TODO: exception handler
    @DeleteMapping("/course:{cid}/student:{sid}")
    public ResponseEntity removeStudentFromCourse(@PathVariable("cid") long cid,
                                        @PathVariable("sid") String sid) throws Exception {
        Course course = courseRepo.findById(cid).orElseThrow(() -> new InvalidRequestParamException("Invalid course id."));
        Student student = studentRepo.findById(sid).orElseThrow(() -> new InvalidRequestParamException("Invalid student id."));
        course.getStudent().remove(student);
        courseRepo.save(course);
        return ResponseEntity.accepted().body("Student removed.");
    }

    @DeleteMapping("/course:{cid}")
    public ResponseEntity removeCourse(@PathVariable("cid") long cid) {
        if (!courseRepo.existsById(cid)) {
            return ResponseEntity.badRequest().body("Invalid course id.");
        }
        courseRepo.deleteById(cid);
        return ResponseEntity.accepted().body("Course removed.");
    }
}
