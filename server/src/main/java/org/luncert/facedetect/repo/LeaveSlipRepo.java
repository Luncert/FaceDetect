package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.LeaveSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveSlipRepo extends JpaRepository<LeaveSlip, Long> {

    List<LeaveSlip> findByStudentID(String studentID);
}
