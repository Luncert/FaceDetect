package org.luncert.facedetect.component;

import com.alibaba.fastjson.JSONObject;
import org.luncert.facedetect.model.UserAccount;
import org.luncert.facedetect.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse rep, Authentication auth)
            throws IOException {
        rep.setHeader("Content-Type", "application/json;charset=UTF-8");
        rep.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        rep.setHeader("Access-Control-Allow-Headers", "token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

        UserAccount account = ((UserInfo) auth.getPrincipal()).getAccount();
        JSONObject json = new JSONObject();
        json.put("identified", true);
        json.put("role", account.getRole());
        rep.getWriter().write(json.toJSONString());
    }
}