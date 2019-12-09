package org.luncert.facedetect.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/teacher/{courseID}")
public class SignInController {

    /**
     *
     * @param cid course id
     * @return signIn id
     */
    @PostMapping("/signIn/start")
    public long startSignIn(@PathVariable("courseID") long cid) {
        return -1;
    }

    @PostMapping("/{signInID}/{studentID}")
    public void signIn(@PathVariable("courseID") long cid,
                       @PathVariable("signInID") long signInID,
                       @PathVariable("studentID") String sid, boolean beLate) {

    }

    @PostMapping("/{signInID}/stop")
    public void stopSignIn(@PathVariable("courseID") long cid,
                           @PathVariable("signInID") long signInID) {

    }

    @GetMapping("/signInRecords")
    public List getSignInRecords(@PathVariable("courseID") long cid) {
        return null;
    }

    @GetMapping("/{signInID}")
    public Object getSignInRecord(@PathVariable("courseID") long cid,
                                  @PathVariable("signInID") long signInID) {
        return null;
    }

    @DeleteMapping("/{signInID}")
    public void removeSignInRecord(@PathVariable("courseID") long cid,
                                   @PathVariable("signInID") long signInID) {

    }
}
