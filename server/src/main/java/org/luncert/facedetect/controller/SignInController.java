package org.luncert.facedetect.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.luncert.facedetect.dto.GetSignInDto;
import org.luncert.facedetect.exception.InvalidRequestParamException;
import org.luncert.facedetect.model.SignIn;
import org.luncert.facedetect.model.SignInRecord;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.repo.AdvanceSignInRepo;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.SignInRecordRepo;
import org.luncert.facedetect.repo.SignInRepo;
import org.luncert.facedetect.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SignIn Record
 */
@RestController
@RequestMapping("/user/teacher/course:{courseID}")
public class SignInController {

    @Autowired
    private SignInRepo signInRepo;

    @Autowired
    private SignInRecordRepo signInRecordRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private AdvanceSignInRepo advanceSignInRepo;

    /**
     *
     * @param cid course id
     * @return signIn id
     */
    @PostMapping("/signIn/start")
    public ResponseEntity startSignIn(@PathVariable("courseID") long cid) {
        if (!courseRepo.existsById(cid)) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }
        SignIn record = new SignIn();
        record.setStartTime(System.currentTimeMillis());
        record.setCourseID(cid);
        record = signInRepo.save(record);

        JSONObject json = new JSONObject();
        json.put("signInID", record.getId());
        return ResponseEntity.ok(json);
    }

    /**
     *
     * @param beLate False=在签到时间内签到 or 签到失败老师手动签到，True=签到时间外老师手动签到
     */
    @PutMapping("/signIn:{signInID}/student:{studentID}")
    public ResponseEntity signIn(@PathVariable("courseID") long cid,
                                 @PathVariable("signInID") long signInID,
                                 @PathVariable("studentID") String studentID, boolean beLate) throws InvalidRequestParamException {
        if (signInRecordRepo.findBySignInIdAndStudentID(signInID, studentID).isPresent()) {
            return ResponseEntity.badRequest().body("Specified student has been sign in.");
        }

        SignIn signIn = signInRepo.findById(signInID).orElseThrow(() -> new InvalidRequestParamException("Invalid sign in id."));
        if (signIn.getCourseID() != cid) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }

        Student student = studentRepo.findById(studentID)
                .orElseThrow(() -> new InvalidRequestParamException("Invalid student id."));

        SignInRecord signInRecord = new SignInRecord();
        signInRecord.setCreateTime(System.currentTimeMillis());
        signInRecord.setSignIn(signIn);
        signInRecord.setStudent(student);
        signInRecord.setBeLate(beLate);
        signInRecordRepo.save(signInRecord);

        return new ResponseEntity(HttpStatus.OK);
    }

    // beLate is not allowed there
    @PutMapping("/signIn:{signInID}/students")
    public ResponseEntity batchSignIn(@PathVariable("courseID") long cid,
                                      @PathVariable("signInID") long signInID,
                                      @RequestBody List<String> studentIdList) {
        JSONObject result = new JSONObject();
        result.put("result", "unprocessed");

        Optional<SignIn> optionalSignIn = signInRepo.findById(signInID);
        if (!optionalSignIn.isPresent()) {
            result.put("description", "Invalid sign in id.");
            return ResponseEntity.badRequest().body(result);
        }

        SignIn signIn = optionalSignIn.get();
        if (signIn.getCourseID() != cid) {
            result.put("description", "Invalid course id.");
            return ResponseEntity.badRequest().body(result);
        }

        JSONArray failureRecords = new JSONArray();
        long currentTime = System.currentTimeMillis();

        result.put("result", "processed");
        for (String studentID : studentIdList) {
            if (signInRecordRepo.findBySignInIdAndStudentID(signInID, studentID).isPresent()) {
                JSONObject failureReason = new JSONObject();
                failureReason.put("studentID", studentID);
                failureReason.put("failureReason", "Specified student has been sign in.");
                failureRecords.add(failureReason);
            } else {
                Optional<Student> optionalStudent = studentRepo.findById(studentID);
                if (!optionalStudent.isPresent()) {
                    JSONObject failureReason = new JSONObject();
                    failureReason.put("studentID", studentID);
                    failureReason.put("failureReason", "Invalid student id.");
                    failureRecords.add(failureReason);
                } else {
                    Student student = optionalStudent.get();

                    SignInRecord signInRecord = new SignInRecord();
                    signInRecord.setCreateTime(currentTime);
                    signInRecord.setSignIn(signIn);
                    signInRecord.setStudent(student);
                    signInRecord.setBeLate(false);
                    signInRecordRepo.save(signInRecord);
                }
            }
        }

        if (failureRecords.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            result.put("external", failureRecords);
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/signIn:{signInID}/stop")
    public ResponseEntity stopSignIn(@PathVariable("courseID") long cid,
                           @PathVariable("signInID") long signInID) {
        Optional<SignIn> optionalSignInRecord = signInRepo.findById(signInID);
        if (!optionalSignInRecord.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid signInID.");
        }
        SignIn record = optionalSignInRecord.orElseThrow(IllegalArgumentException::new);
        if (record.getCourseID() != cid) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }
        record.setEndTime(System.currentTimeMillis());
        signInRepo.save(record);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/signInList")
    public List<GetSignInDto> getSignInList(@PathVariable("courseID") long cid) {
        return advanceSignInRepo.getStatistics(cid);
    }

    @GetMapping("/signIn:{signInID}")
    public ResponseEntity getSignInRecordList(@PathVariable("courseID") long cid,
                                                  @PathVariable("signInID") long signInID) {
        return ResponseEntity.ok(signInRecordRepo.findBySignInID(signInID));
    }

    @DeleteMapping("/signIn:{signInID}/signInRecord:{signInRecordID}")
    public void removeSignInRecord(@PathVariable("courseID") long cid,
                                   @PathVariable("signInID") long signInID,
                                   @PathVariable("signInRecordID") long signInRecordID) {
        signInRecordRepo.deleteById(signInRecordID);
    }

    @DeleteMapping("/signIn:{signInID}")
    public void removeSignIn(@PathVariable("courseID") long cid,
                             @PathVariable("signInID") long signInID) {
        signInRecordRepo.deleteBySignInID(signInID);
        signInRepo.deleteById(signInID);
    }
}
