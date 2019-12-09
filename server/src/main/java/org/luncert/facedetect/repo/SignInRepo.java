package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.SignIn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignInRepo extends CrudRepository<SignIn, Long> {

  List<SignIn> findAllByCourseID(Long courseID);
}
