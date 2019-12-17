package org.luncert.facedetect.controller;

import com.alibaba.fastjson.JSONObject;
import org.luncert.facedetect.dto.GetSignInDto;
import org.luncert.facedetect.exception.InvalidRequestParamException;
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
import java.util.stream.Collectors;

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

    /**
     *
     * @param cid course id
     * @return signIn id
     */
    @PostMapping("/signIn/start")
    public String startSignIn(@PathVariable("courseID") long cid) {
        SignIn record = new SignIn();
        record.setStartTime(System.currentTimeMillis());
        record.setCourseID(cid);
        record = signInRepo.save(record);

        JSONObject json = new JSONObject();
        json.put("signInID", record.getId());
        return json.toJSONString();
    }

    /**
     *
     * @param cid
     * @param signInID
     * @param studentID
     * @param beLate False=在签到时间内签到 or 签到失败老师手动签到，True=签到时间外老师手动签到
     * @return
     */
    @PutMapping("/signIn:{signInID}/student:{studentID}")
    public ResponseEntity signIn(@PathVariable("courseID") long cid,
                                 @PathVariable("signInID") long signInID,
                                 @PathVariable("studentID") String studentID, boolean beLate) throws InvalidRequestParamException {
        Student student = studentRepo.findById(studentID)
                .orElseThrow(() -> new InvalidRequestParamException("Invalid student id."));

        if (signInRecordRepo.findByStudent(student).isPresent()) {
            return ResponseEntity.badRequest().body("Specified student has been sign in.");
        }

        SignIn signIn = signInRepo.findById(signInID).orElseThrow(() -> new InvalidRequestParamException("Invalid sign in id."));
        if (signIn.getCourseID() != cid) {
            return ResponseEntity.badRequest().body("Invalid courseID.");
        }

        SignInRecord signInRecord = new SignInRecord();
        signInRecord.setStudent(student);
        signInRecord.setBeLate(beLate);
        signInRecordRepo.save(signInRecord);

        signIn.getSignInRecords().add(signInRecord);
        signInRepo.save(signIn);

        return new ResponseEntity(HttpStatus.OK);
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
        List<SignIn> signInList = signInRepo.findAllByCourseID(cid);
        return signInList.stream()
                .map(s -> new GetSignInDto(s.getId(), s.getStartTime(), s.getEndTime(), s.getCourseID()))
                .collect(Collectors.toList());
    }

    @GetMapping("/signIn:{signInID}")
    public ResponseEntity getSignInRecordList(@PathVariable("courseID") long cid,
                                                  @PathVariable("signInID") long signInID) {
        Optional<SignIn> optionalSignInRecord = signInRepo.findById(signInID);
        if (!optionalSignInRecord.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid signInID.");
        }
        SignIn record = optionalSignInRecord.orElseThrow(IllegalArgumentException::new);
        return ResponseEntity.ok(record.getSignInRecords());
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
        signInRepo.deleteById(signInID);
    }
}
