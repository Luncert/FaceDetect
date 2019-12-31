package org.luncert.facedetect.repo;

import org.luncert.facedetect.dto.BasicStudentDto;
import org.luncert.facedetect.dto.GetSignInDto;
import org.luncert.facedetect.model.SignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdvanceSignInRepo {

  @Autowired
  private SignInRepo signInRepo;

  @PersistenceContext
  private EntityManager entityManager;

  @SuppressWarnings("unchecked")
  public List<GetSignInDto> getStatistics(Long courseID) {
    List<SignIn> signInList = signInRepo.findAllByCourseID(courseID);
    List<GetSignInDto> ret = new ArrayList<>();
    for (SignIn signIn : signInList) {
      GetSignInDto dto = new GetSignInDto();
      dto.setId(signIn.getId());
      dto.setStartTime(signIn.getStartTime());
      dto.setEndTime(signIn.getEndTime());

      List<BasicStudentDto> lateStudents = new ArrayList<>();
      String sql = "SELECT st.id, st.name FROM sign_in_record sr" +
          " LEFT JOIN student st ON sr.student_id=st.id" +
          " WHERE sr.be_late AND sr.sign_in_id=" + signIn.getId();
      List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
      for (Object[] column : result) {
        lateStudents.add(new BasicStudentDto((String) column[0], (String) column[1], false));
      }
      dto.setLateStudentList(lateStudents);

      List<BasicStudentDto> nonSignedInStudents = new ArrayList<>();
      sql = "SELECT st.id, st.name FROM student st" +
          " LEFT JOIN course_student cs ON st.id=cs.student_id" +
          " WHERE cs.course_id=" + courseID + " AND st.id NOT IN" +
          " (SELECT sr.student_id from sign_in_record sr WHERE sr.sign_in_id=" + signIn.getId() + ")";
      result = entityManager.createNativeQuery(sql).getResultList();
      for (Object[] column : result) {
        nonSignedInStudents.add(new BasicStudentDto((String) column[0], (String) column[1], false));
      }
      dto.setNonSignedInStudentList(nonSignedInStudents);

      ret.add(dto);
    }
    return ret;
  }
}
