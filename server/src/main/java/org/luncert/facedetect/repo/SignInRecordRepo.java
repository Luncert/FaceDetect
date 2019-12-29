package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.SignInRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignInRecordRepo extends CrudRepository<SignInRecord, Long> {

  @Query(value = "SELECT * FROM sign_in_record sr WHERE sr.sign_in_id=?1", nativeQuery = true)
  List<SignInRecord> findBySignInID(Long signInID);

  @Query(value = "SELECT * FROM sign_in_record sr WHERE sr.sign_in_id=?1 and sr.student_id=?2", nativeQuery = true)
  Optional<SignInRecord> findBySignInIdAndStudentID(Long signInID, String studentID);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM sign_in_record WHERE sign_in_id=?1", nativeQuery = true)
  void deleteBySignInID(Long signInID);
}
