package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends CrudRepository<Student, String> {
}
