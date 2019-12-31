package integration;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luncert.facedetect.App;
import org.luncert.facedetect.controller.ResourceController;
import org.luncert.facedetect.model.Attachment;
import org.luncert.facedetect.model.LeaveSlip;
import org.luncert.facedetect.model.Student;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.repo.LeaveSlipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import({Config.class})
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataGenerator dataGenerator;

    private MockHttpSession session;

    @Autowired
    private LeaveSlipRepo leaveSlipRepo;

    @Before
    public void setup() throws Exception {
        Triple<Student, UserInfo, String> triple = dataGenerator.genStudent();
        UserInfo studentAccount = triple.getMiddle();
        session = Util.authorize(mockMvc, studentAccount.getUsername(), triple.getRight());
    }

    @After
    public void cleanDatabase() {
        dataGenerator.cleanDatabase();
    }

    @Test
    public void testGetLeaveSlipAttachment() throws Exception {
        LeaveSlip la = new LeaveSlip();
        Attachment attachment = new Attachment("test.txt", RandomStringUtils.randomAlphabetic(1024).getBytes());
        la.setAttachment(attachment);
        leaveSlipRepo.save(la);

        mockMvc.perform(
                get(ResourceController.linkLeaveSlipAttachment(la.getId(), "test.txt"))
                        .session(session))
                .andExpect(status().isOk())
        .andExpect(content().bytes(attachment.getData()));
    }
}
