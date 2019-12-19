package org.luncert.facedetect.repo;

import org.luncert.facedetect.controller.ResourceController;
import org.luncert.facedetect.dto.StudentGetLeaveSlipDto;
import org.luncert.facedetect.dto.TeacherGetLeaveSlipDto;
import org.luncert.facedetect.model.LeaveSlip;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdvanceLeaveSlipRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<StudentGetLeaveSlipDto> findByStudentID(String sid) {
        String sql = "SELECT l.id, (SELECT name FROM course WHERE id=l.courseID), state, create_time, date, content, attachment_name" +
                " FROM leave_slip l" +
                " WHERE l.studentID=\"" + sid + "\"";
        List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
        List<StudentGetLeaveSlipDto> ret = new ArrayList<>();
        for (Object[] column : result) {
            StudentGetLeaveSlipDto dto = new StudentGetLeaveSlipDto();
            Long id = ((BigInteger) column[0]).longValue();
            dto.setCourseName((String) column[1]);
            dto.setState(LeaveSlip.State.values()[(Integer) column[2]]);
            dto.setCreateTime(((BigInteger) column[3]).longValue());
            dto.setDate((String) column[4]);
            dto.setContent(new String((byte[]) column[5]));
            dto.setAttachmentUrl(ResourceController.linkLeaveSlipAttachment(id, (String) column[6]));
            ret.add(dto);
        }
        return ret;
    }

    public List<TeacherGetLeaveSlipDto> findByTeacherID(Long tid) {
        String sql = "SELECT l.id, c.id courseID, c.name, l.studentID," +
                " (SELECT name FROM student WHERE id=l.studentID)," +
                " state, create_Time, date, content, attachment_name" +
                " FROM leave_slip l LEFT JOIN course c ON l.courseID=c.id" +
                " WHERE c.teacherID=" + tid;
        List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
        List<TeacherGetLeaveSlipDto> ret = new ArrayList<>();
        for (Object[] column : result) {
            TeacherGetLeaveSlipDto dto = new TeacherGetLeaveSlipDto();
            Long id = ((BigInteger) column[0]).longValue();
            dto.setId(id);
            dto.setCourseID(((BigInteger) column[1]).longValue());
            dto.setCourseName((String) column[2]);
            dto.setStudentID((String) column[3]);
            dto.setStudentName((String) column[4]);

            dto.setState(LeaveSlip.State.values()[(Integer) column[5]]);
            dto.setCreateTime(((BigInteger) column[6]).longValue());
            dto.setDate((String) column[7]);
            dto.setContent(new String((byte[]) column[8]));
            dto.setAttachmentUrl(ResourceController.linkLeaveSlipAttachment(id, (String) column[9]));

            ret.add(dto);
        }
        return ret;
    }
}
