package org.luncert.facedetect.controller;

import org.luncert.facedetect.model.Attachment;
import org.luncert.facedetect.repo.LeaveSlipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    @GetMapping("/leaveSlipAttachment:{id}/{name:.*}")
    @PreAuthorize("hasRole('Student') or hasRole('Teacher')")
    public void getLeaveSlipAttachment(@PathVariable("id") Long id,
                                                 @PathVariable("name") String name,
                                                 HttpServletResponse response) throws IOException {
        Attachment attachment = leaveSlipRepo.findById(id).orElseThrow(IllegalArgumentException::new).getAttachment();
        response.setStatus(200);
        response.setHeader("Content-Type", "application/force-download");
        response.getOutputStream().write(attachment.getData());
    }

    public static String linkLeaveSlipAttachment(Long leaveSlipID, String rawName) {
        return "/resource/leaveSlipAttachment:" + leaveSlipID + "/" + rawName;
    }
}
