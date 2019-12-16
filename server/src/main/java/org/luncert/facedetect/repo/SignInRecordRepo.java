package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.SignInRecord;
import org.luncert.facedetect.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignInRecordRepo extends CrudRepository<SignInRecord, Long> {

  Optional<SignInRecord> findByStudent(Student student);
}
