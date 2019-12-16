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
import org.luncert.facedetect.model.Course;
import org.luncert.facedetect.model.SignIn;
import org.luncert.facedetect.model.SignInRecord;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.Teacher;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.repo.CourseRepo;
import org.luncert.facedetect.repo.SignInRecordRepo;
import org.luncert.facedetect.repo.SignInRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import({Config.class})
public class SignInControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DataGenerator dataGenerator;

  private MockHttpSession session;

  private Course course;

  @Autowired
  private CourseRepo courseRepo;

  @Autowired
  private SignInRepo signInRepo;

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

    course = new Course();
    course.setName(RandomStringUtils.randomAlphabetic(6));
    course.setTeacher(teacher);
    course.setStudent(studentSet);
    courseRepo.save(course);
  }

  @After
  public void clean() {
    dataGenerator.cleanDatabase();
  }

  @Test
  public void testStartSignIn() throws Exception {
    String rep = mockMvc.perform(
        post("/user/teacher/course:{0}/signIn/start", course.getId())
            .session(session))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    JSONObject json = JSONObject.parseObject(rep);
    Assert.assertTrue(json.containsKey("signInID"));
    long signInID = json.getLong("signInID");
    Assert.assertTrue(signInRepo.existsById(signInID));
  }

  @Test
  public void testSignIn() throws Exception {
    final Student student = course.getStudent().toArray(new Student[0])[0];

    SignIn signIn = new SignIn();
    signIn.setCourseID(course.getId());
    signIn.setStartTime(System.currentTimeMillis());
    signIn = signInRepo.save(signIn);

    mockMvc.perform(
        put("/user/teacher/course:{0}/signIn:{1}/student:{2}", course.getId(), signIn.getId(), student.getId())
        .session(session))
        .andExpect(status().isOk());
    // check the responsive SignInRecord has been created
    signIn =  signInRepo.findById(signIn.getId()).orElseThrow();
    List<SignInRecord> signInRecords = signIn.getSignInRecords();
    Assert.assertEquals(1, signInRecords.size());
    Assert.assertEquals(student.getId(), signInRecords.get(0).getStudent().getId());
  }

  @Test
  public void testStopSignIn() throws Exception {
    SignIn signIn = new SignIn();
    signIn.setCourseID(course.getId());
    signIn.setStartTime(System.currentTimeMillis());
    signIn = signInRepo.save(signIn);

    mockMvc.perform(
            put("/user/teacher/course:{0}/signIn:{1}/stop", course.getId(),  signIn.getId())
                    .session(session))
            .andExpect(status().isOk());

    signIn = signInRepo.findById(signIn.getId()).orElseThrow();
    Assert.assertTrue(signIn.getStartTime() < signIn.getEndTime());
  }

  @Test
  public void testGetSignIn() throws Exception {
    mockMvc.perform(
            get("/user/teacher/course:{0}/signInList", course.getId())
                    .session(session))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }
}
