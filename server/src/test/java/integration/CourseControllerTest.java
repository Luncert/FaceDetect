package integration;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luncert.facedetect.App;
import org.luncert.facedetect.dto.BasicCourseDto;
import org.luncert.facedetect.dto.BasicStudentDto;
import org.luncert.facedetect.dto.CreateCourseDto;
import org.luncert.facedetect.dto.UpdateCourseDto;
import org.luncert.facedetect.model.Course;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.Teacher;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import({Config.class})
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataGenerator dataGenerator;

    private MockHttpSession session;

    @Autowired
    private CourseRepo courseRepo;

    private List<Course> courseList;

    @Before
    public void setup() throws Exception {
        Triple<Teacher, UserInfo, String> triple = dataGenerator.genTeacher();
        Teacher teacher = triple.getLeft();
        UserInfo teacherAccount = triple.getMiddle();

        session = Util.authorize(mockMvc, teacherAccount.getUsername(), triple.getRight());

        Set<Student> studentSet = new LinkedHashSet<>();
        for (int i = 0; i < 10; i++) {
            studentSet.add(dataGenerator.genStudent().getLeft());
        }

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
    public void testCreateCourse() throws Exception {
        CreateCourseDto createCourseDto = new CreateCourseDto();
        createCourseDto.setName("English Lesson");
        Set<String> studentIDSet = new LinkedHashSet<>();
        for (int i = 0; i < 10; i++) {
            Triple<Student, UserInfo, String> triple = dataGenerator.genStudent();
            studentIDSet.add(triple.getLeft().getId());
        }
        createCourseDto.setStudentIDList(studentIDSet);

        mockMvc.perform(post("/user/teacher/course")
                .content(JSONObject.toJSONBytes(createCourseDto))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourses() throws Exception {
        List<BasicCourseDto> basicCourseDtoList = courseList.stream()
                .map(course -> new BasicCourseDto(course.getId(), course.getName()))
                .collect(Collectors.toList());

        mockMvc.perform(get("/user/teacher/courses")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(JSONObject.toJSONString(basicCourseDtoList)));
    }

    @Test
    public void testGetCourseStudents() throws Exception {
        final Course course = courseList.get(0);

        List<BasicStudentDto> studentList = course.getStudent().stream()
                .map(s -> new BasicStudentDto(s.getId(), s.getName(), s.getFaceData() == null))
                .collect(Collectors.toList());

        mockMvc.perform(get("/user/teacher/course:{0}/students", course.getId())
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().json(JSONObject.toJSONString(studentList)));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        final Course course = courseList.get(0);

        UpdateCourseDto updateCourseDto = new UpdateCourseDto(RandomStringUtils.randomAlphabetic(12));

        mockMvc.perform(put("/user/teacher/course:{0}", course.getId())
                        .content(JSONObject.toJSONBytes(updateCourseDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());

        Course c = courseRepo.findById(course.getId()).orElseThrow();
        Assert.assertNotEquals(course.getName(), c.getName());
        Assert.assertEquals(updateCourseDto.getName(), c.getName());
    }

    @Test
    public void testAddStudentToCourse() throws Exception {
        final Course course = courseList.get(0);

        Student student = dataGenerator.genStudent().getLeft();

        mockMvc.perform(put("/user/teacher/course:{0}/student:{1}", course.getId(), student.getId())
                .session(session))
                .andExpect(status().isOk());

        Course c = courseRepo.findById(course.getId()).orElseThrow();
        Assert.assertTrue(c.getStudent().contains(student));
    }

    @Test
    public void testRemoveStudentFromCourse() throws Exception {
        final Course course = courseList.get(0);
        final Student student = course.getStudent().toArray(new Student[0])[0];

        mockMvc.perform(delete("/user/teacher/course:{0}/student:{1}", course.getId(), student.getId())
                .session(session))
                .andExpect(status().isAccepted());

        Course c = courseRepo.findById(course.getId()).orElseThrow();
        Assert.assertFalse(c.getStudent().contains(student));
    }

    @Test
    public void testRemoveCourse() throws Exception {
        final Course course = courseList.get(0);

        mockMvc.perform(delete("/user/teacher/course:{0}", course.getId())
                .session(session))
                .andExpect(status().isAccepted());

        Assert.assertFalse(courseRepo.existsById(course.getId()));
    }
}
