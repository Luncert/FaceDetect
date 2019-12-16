package integration;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luncert.facedetect.App;
import org.luncert.facedetect.controller.ResourceController;
import org.luncert.facedetect.dto.LeaveSlipProcessResultDto;
import org.luncert.facedetect.dto.TeacherGetLeaveSlipDto;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.LeaveSlipRepo;
import org.luncert.facedetect.repo.SignInRecordRepo;
import org.luncert.facedetect.repo.SignInRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import({Config.class})
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataGenerator dataGenerator;

    private MockHttpSession session;

    private Teacher teacher;

    private List<Course> courseList;

    private List<Student> studentList;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    @Autowired
    private SignInRepo signInRepo;

    @Autowired
    private SignInRecordRepo signInRecordRepo;

    @Before
    public void setup() throws Exception {
        Triple<Teacher, UserInfo, String> triple = dataGenerator.genTeacher();
        teacher = triple.getLeft();
        UserInfo teacherAccount = triple.getMiddle();

        session = Util.authorize(mockMvc, teacherAccount.getUsername(), triple.getRight());

        studentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Student student = dataGenerator.genStudent().getLeft();
            studentList.add(student);
        }
        Set<Student> studentSet = new HashSet<>(studentList);

        courseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Course course = new Course();
            course.setName(RandomStringUtils.randomAlphabetic(6));
            course.setTeacher(teacher);
            course.setStudent(studentSet);
            courseRepo.save(course);
            courseList.add(course);
        }
    }

    @Test
    public void testGetLeaveSlipList() throws Exception {
        List<TeacherGetLeaveSlipDto> leaveSlipDtoList = new ArrayList<>();
        for (Course course : courseList) {
            Student student = studentList.get(RandomUtils.nextInt(0, 10));
            LeaveSlip l = new LeaveSlip();
            l.setStudentID(student.getId());
            l.setCourseID(course.getId());
            l.setState(LeaveSlip.State.UnProcessed);
            l.setCreateTime(System.currentTimeMillis());
            l.setDate("2016/12/11");
            l.setContent(RandomStringUtils.randomAlphabetic(512));
            Attachment attachment = new Attachment("test.txt", RandomStringUtils.randomAlphabetic(128).getBytes());
            l.setAttachment(attachment);
            l = leaveSlipRepo.save(l);

            leaveSlipDtoList.add(new TeacherGetLeaveSlipDto(l.getId(), course.getName(), student.getId(), student.getName(),
                    l.getState(), l.getCreateTime(), l.getDate(), l.getContent(),
                    ResourceController.linkLeaveSlipAttachment(l.getId(), "test.txt")));
        }

        mockMvc.perform(
                get("/user/teacher/leaveSlips")
                        .session(session))
                .andExpect(status().isOk())
        .andExpect(content().json(JSON.toJSONString(leaveSlipDtoList)));
    }

    @Test
    public void testProcessLeaveSlip() throws Exception {
        final Course course = courseList.get(0);
        final Student student = studentList.get(0);

        LeaveSlip l = new LeaveSlip();
        l.setStudentID(student.getId());
        l.setCourseID(course.getId());
        l.setState(LeaveSlip.State.UnProcessed);
        l.setCreateTime(System.currentTimeMillis());
        l.setDate("2016/12/11");
        l.setContent(RandomStringUtils.randomAlphabetic(512));
        Attachment attachment = new Attachment("test.txt", RandomStringUtils.randomAlphabetic(128).getBytes());
        l.setAttachment(attachment);
        l = leaveSlipRepo.save(l);

        SignIn signIn = new SignIn();
        signIn.setCourseID(course.getId());
        signIn.setStartTime(System.currentTimeMillis());
        signIn = signInRepo.save(signIn);

        LeaveSlipProcessResultDto resultDto = new LeaveSlipProcessResultDto(true, signIn.getId(), "no comment.");

        mockMvc.perform(
                put("/user/teacher/leaveSlip:{0}", l.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONBytes(resultDto))
                        .session(session))
                .andExpect(status().isOk());

        SignInRecord signInRecord = signInRecordRepo.findByStudent(student).orElseThrow();
        l = signInRecord.getLeaveSlip();
        Assert.assertNotNull(l);
        Assert.assertEquals(LeaveSlip.State.Approved, l.getState());
    }
}
