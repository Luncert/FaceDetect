package org.luncert.facedetect.controller;

import org.luncert.facedetect.model.SignIn;
import org.luncert.facedetect.model.SignInRecord;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.repo.SignInRecordRepo;
import org.luncert.facedetect.repo.SignInRepo;
import org.luncert.facedetect.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * SignIn Record
 */
@RestController
@RequestMapping("/user/teacher/{courseID}")
public class SignInController {

    @Autowired
    private SignInRepo signInRepo;

    @Autowired
    private SignInRecordRepo signInRecordRepo;

    @Autowired
    private StudentRepo studentRepo;

    /**
     *
     * @param cid course id
     * @return signIn id
     */
    @PostMapping("/signIn/start")
    public long startSignIn(@PathVariable("courseID") long cid) {
        SignIn record = new SignIn();
        record.setStartTime(System.currentTimeMillis());
        record.setCourseID(cid);
        record = signInRepo.save(record);
        return record.getId();
    }

    /**
     *
     * @param cid
     * @param signInID
     * @param sid
     * @param beLate False=在签到是时间内签到 or 签到失败老师手动签到，True=签到时间外老师手动签到
     * @return
     */
    @PostMapping("/{signInID}/{studentID}")
    public ResponseEntity signIn(@PathVariable("courseID") long cid,
                                 @PathVariable("signInID") long signInID,
                                 @PathVariable("studentID") String sid, boolean beLate) {
        Optional<SignIn> optionalSignInRecord = signInRepo.findById(signInID);
        if (!optionalSignInRecord.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid signInID.");
        }
        SignIn record = optionalSignInRecord.get();
        if (record.getCourseID() != cid) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }
        Optional<Student> optionalStudent = studentRepo.findById(sid);
        if (!optionalStudent.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid studentID.");
        }
        Student student = optionalStudent.get();

        SignInRecord signInRecord = new SignInRecord();
        signInRecord.setStudent(student);
        signInRecord.setBeLate(beLate);
        signInRecordRepo.save(signInRecord);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/{signInID}/stop")
    public ResponseEntity stopSignIn(@PathVariable("courseID") long cid,
                           @PathVariable("signInID") long signInID) {
        Optional<SignIn> optionalSignInRecord = signInRepo.findById(signInID);
        if (!optionalSignInRecord.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid signInID.");
        }
        SignIn record = optionalSignInRecord.get();
        if (record.getCourseID() != cid) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }
        record.setEndTime(System.currentTimeMillis());
        signInRepo.save(record);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/signInRecords")
    public List getSignInRecords(@PathVariable("courseID") long cid) {
        return signInRepo.findAllByCourseID(cid);
    }

    @GetMapping("/{signInID}")
    public ResponseEntity getSignInRecord(@PathVariable("courseID") long cid,
                                                  @PathVariable("signInID") long signInID) {
        Optional<SignIn> optionalSignInRecord = signInRepo.findById(signInID);
        if (!optionalSignInRecord.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid signInID.");
        }
        SignIn record = optionalSignInRecord.get();
        return ResponseEntity.ok(record.getSignInRecords());
    }

    @DeleteMapping("/{signInID}")
    public void removeSignInRecord(@PathVariable("courseID") long cid,
                                   @PathVariable("signInID") long signInID) {
        signInRepo.deleteById(signInID);
    }
}
