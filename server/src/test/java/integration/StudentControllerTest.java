package integration;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luncert.facedetect.App;
import org.luncert.facedetect.controller.ResourceController;
import org.luncert.facedetect.dto.StudentGetCourseDto;
import org.luncert.facedetect.dto.StudentGetLeaveSlipDto;
import org.luncert.facedetect.dto.StudentProfileDto;
import org.luncert.facedetect.model.*;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.LeaveSlipRepo;
import org.luncert.facedetect.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import({Config.class})
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataGenerator dataGenerator;

    private Student student;

    private MockHttpSession session;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    private List<Course> courseList;

    @Before
    public void setup() throws Exception {
        Triple<Student, UserInfo, String> triple = dataGenerator.genStudent();
        student = triple.getLeft();
        UserInfo studentAccount = triple.getMiddle();
        session = Util.authorize(mockMvc, studentAccount.getUsername(), triple.getRight());

        Teacher teacher = dataGenerator.genTeacher().getLeft();

        Set<Student> studentSet = Collections.singleton(student);
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

    @After
    public void cleanDatabase() {
        dataGenerator.cleanDatabase();
    }

    @Test
    public void testGetProfile() throws Exception {
        StudentProfileDto profileDto = new StudentProfileDto(student.getName());

        mockMvc.perform(
                get("/user/student/profile")
                        .session(session))
                .andExpect(status().isOk())
        .andExpect(content().json(JSON.toJSONString(profileDto)));
    }

    @Test
    public void testUpdateFaceData() throws Exception {
        byte[] faceData = RandomStringUtils.randomAlphanumeric(1024).getBytes();

        mockMvc.perform(
                multipart("/user/student/faceData")
                        .file("faceData", faceData)
                        .session(session))
                .andExpect(status().isOk());

        final Student s = studentRepo.findById(student.getId()).orElseThrow(IllegalArgumentException::new);
        Assert.assertArrayEquals(faceData, s.getFaceData());
    }

    @Test
    public void testGetCourses() throws Exception {
        List<StudentGetCourseDto> studentCourseDtoList = courseList.stream()
                .map(c -> new StudentGetCourseDto(c.getId(), c.getName(), c.getTeacher().getName()))
                .collect(Collectors.toList());

        mockMvc.perform(
                get("/user/student/courses")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(studentCourseDtoList)));
    }

    @Test
    public void testGetCourseSignInRecords() throws Exception {
        final Course course = courseList.get(0);

        mockMvc.perform(
                get("/user/student/course:{0}/signInRecords", course.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testApplyForLeave() throws Exception {
        String date = "2016/12/8";
        String content = RandomStringUtils.randomAlphanumeric(1024);
        byte[] attachment = RandomStringUtils.randomAlphanumeric(1024).getBytes();

        mockMvc.perform(
                multipart("/user/student/leaveSlip")
                        .file("date", date.getBytes())
                        .file("content", content.getBytes())
                        .file("attachment", attachment)
                        .session(session))
                .andExpect(status().isOk());

        List<LeaveSlip> leaveSlipList = leaveSlipRepo.findByStudentID(student.getId());
        Assert.assertEquals(1, leaveSlipList.size());
        LeaveSlip leaveSlip = leaveSlipList.get(0);
        Assert.assertEquals(date, leaveSlip.getDate());
        Assert.assertEquals(content, leaveSlip.getContent());
        Assert.assertArrayEquals(attachment, leaveSlip.getAttachment().getData());
    }

    @Test
    public void testGetLeaveSlipList() throws Exception {
        final Course course = courseList.get(0);

        LeaveSlip la = new LeaveSlip();
        la.setStudentID(student.getId());
        la.setCourseID(course.getId());
        la.setState(LeaveSlip.State.UnProcessed);
        la.setCreateTime(System.currentTimeMillis());
        la.setDate("2016/12/11");
        la.setContent(RandomStringUtils.randomAlphabetic(512));
        Attachment attachment = new Attachment("test.txt", RandomStringUtils.randomAlphabetic(1024).getBytes());
        la.setAttachment(attachment);
        leaveSlipRepo.save(la);

        List<StudentGetLeaveSlipDto> studentLeaveSlipDtoList = Collections.singletonList(
                new StudentGetLeaveSlipDto(course.getName(), la.getState(), la.getCreateTime(), la.getDate(), la.getContent(),
                        ResourceController.linkLeaveSlipAttachment(la.getId(), la.getAttachment().getName())));

        mockMvc.perform(
                get("/user/student/leaveSlips")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(studentLeaveSlipDtoList)));
    }
}
