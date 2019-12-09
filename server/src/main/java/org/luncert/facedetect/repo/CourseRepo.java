package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends CrudRepository<Course, Long> {

  @Query(value = "SELECT * FROM course WHERE teacherID=?1", nativeQuery = true)
  List<Course> findByTeacherID(Long teacherID);
}
