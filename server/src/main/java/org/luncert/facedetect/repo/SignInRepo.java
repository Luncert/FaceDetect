package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.SignIn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignInRepo extends CrudRepository<SignIn, Long> {

  @Query(value = "SELECT id, start_time, end_time, courseID FROM sign_in WHERE courseID=?1", nativeQuery = true)
  List<SignIn> findAllByCourseID(Long courseID);
}
