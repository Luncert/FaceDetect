package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> {

  @Query(value = "SELECT id, name, teacherID FROM course WHERE teacherID=?1", nativeQuery = true)
  List<Course> findByTeacherID(Long teacherID);

  @Query(value = "SELECT c.id, c.name, c.teacherID FROM course c" +
          " LEFT JOIN course_student cs ON c.id=cs.course_id" +
          " WHERE cs.student_id=?1", nativeQuery = true)
  List<Course> findByStudentID(String studentID);
}
