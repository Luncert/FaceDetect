package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.SignInRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignInRecordRepo extends CrudRepository<SignInRecord, Long> {
}
