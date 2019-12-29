package org.luncert.facedetect.controller;

import org.luncert.facedetect.dto.*;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/profile")
    public BasicStudentDto getProfile(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        Student s = studentRepo.findById(sid).orElseThrow(IllegalAccessError::new);
        return new BasicStudentDto(s.getId(), s.getName(), s.getFaceData() != null);
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
    @SuppressWarnings("unchecked")
    public List<StudentGetCourseDto> getCourses(Authentication authentication) {
        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
        String sid = account.getObjectID();
        List<Course> courseList = courseRepo.findByStudentID(sid);
        List<StudentGetCourseDto> ret = new ArrayList<>();
        for (Course course : courseList) {
            StudentGetCourseDto dto = new StudentGetCourseDto();
            dto.setCourseID(course.getId());
            dto.setCourseName(course.getName());
            dto.setTeacherName(course.getTeacher().getName());

            int lateTimes = 0;

            Map<Long, CourseSignInDetailDto> signInDetailDtoMap = new HashMap<>();
            String sql = "SELECT sr.sign_in_id, si.start_time, si.end_time, sr.be_late" +
                " FROM sign_in_record sr" +
                " LEFT JOIN sign_in si ON sr.sign_in_id=si.id" +
                " WHERE si.courseID=" + course.getId() + " AND sr.student_id=\"" + sid + "\"";
            List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
            for (Object[] column : result) {
                boolean beLate = (Boolean) column[3];
                signInDetailDtoMap.put(bitIntegerToLong(column[0]),
                    new CourseSignInDetailDto(bitIntegerToLong(column[1]), bitIntegerToLong(column[2]), beLate, false));
                if (beLate) {
                    lateTimes++;
                }
            }

            int signedInTimes = signInDetailDtoMap.size();

            sql = "SELECT sr.sign_in_id, si.start_time, si.end_time" +
                " FROM sign_in_record sr" +
                " LEFT JOIN sign_in si ON sr.sign_in_id=si.id" +
                " WHERE si.courseID=" + course.getId() +
                " GROUP BY sr.sign_in_id, sr.be_late";
            result = entityManager.createNativeQuery(sql).getResultList();
            for (Object[] column : result) {
                long signInID = bitIntegerToLong(column[0]);
                if (!signInDetailDtoMap.containsKey(signInID)) {
                    signInDetailDtoMap.put(signInID,
                        new CourseSignInDetailDto(bitIntegerToLong(column[1]), bitIntegerToLong(column[2]), false, true));
                }
            }

            dto.setLateTimes(lateTimes);
            dto.setNonSignedInTimes(signInDetailDtoMap.size() - signedInTimes);
            dto.setSignInDetails(new ArrayList<>(signInDetailDtoMap.values()));
            ret.add(dto);
        }
        return ret;
    }

//    @GetMapping("/course:{courseID}/signInRecords")
//    public List<CourseSignInRecordDto> getCourseSignInRecords(Authentication authentication,
//                                                              @PathVariable("courseID") long cid) {
//        UserAccount account = ((UserInfo) authentication.getPrincipal()).getAccount();
//        String sid = account.getObjectID();
//        return advanceSignInRecordRepo.findByStudentIdAndCourseId(sid, cid);
//    }

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
        if (attachment != null && attachment.getOriginalFilename() != null) {
            String suffix = attachment.getOriginalFilename();
            suffix = suffix.substring(suffix.lastIndexOf("."));
            la.setAttachment(new Attachment(UUID.randomUUID().toString() + suffix, attachment.getBytes()));
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

    private Long bitIntegerToLong(Object value) {
        return ((BigInteger) value).longValue();
    }
}
