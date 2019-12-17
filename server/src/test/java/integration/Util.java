package integration;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class Util {

    private static final String CHARSET = StandardCharsets.UTF_8.name();

    static MockHttpSession authorize(MockMvc mockMvc, String account, String password) throws Exception {
        String credential = URLEncoder.encode("account", CHARSET)
                + '=' + URLEncoder.encode(account, CHARSET)
                + '&' + URLEncoder.encode("password", CHARSET)
                + '=' + URLEncoder.encode(password, CHARSET);
//        String credential = null;

        ResultActions resultActions = mockMvc.perform(
                post("/user/signIn")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(credential));

        resultActions.andExpect(status().isOk());
//        resultActions.andExpect(header().stringValues("Location", "/user"));

        MvcResult result = resultActions.andReturn();
        MockHttpServletRequest req = result.getRequest();
        return (MockHttpSession) req.getSession();
    }
}
