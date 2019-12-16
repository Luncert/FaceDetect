package org.luncert.facedetect.repo;

import org.luncert.facedetect.dto.CourseSignInRecordDto;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdvanceSignInRecordRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<CourseSignInRecordDto> findByStudentIdAndCourseId(String studentID, Long courseID) {
        String sql = "SELECT s.start_time, s.end_time, r.create_time, r.be_late, r.leave_slipid  FROM" +
                " (SELECT * FROM sign_in_record WHERE studentID=\"" + studentID + "\") r" +
                " LEFT JOIN" +
                " (SELECT * FROM sign_in WHERE courseID=" + courseID + ") s" +
                " ON r.recordID=s.id";
        List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
        List<CourseSignInRecordDto> ret = new ArrayList<>();
        for (Object[] column : result) {
            CourseSignInRecordDto dto = new CourseSignInRecordDto();
            dto.setStartTime((Long)column[0]);
            dto.setEndTime((Long)column[1]);
            dto.setRecordTime((Long)column[2]);
            dto.setBeLate((Boolean)column[3]);
            dto.setHasLeaveSlip(column[4] != null);
            ret.add(dto);
        }
        return ret;
    }
}
