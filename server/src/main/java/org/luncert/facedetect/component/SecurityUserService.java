package org.luncert.facedetect.component;

import org.luncert.facedetect.model.UserAccount;
import org.luncert.facedetect.model.UserInfo;
import org.luncert.facedetect.repo.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepo.findByAccount(UserAccount.fromString(s));
        if (userInfo == null) {
            throw new UsernameNotFoundException("Invalid account.");
        }
        return userInfo;
    }
}