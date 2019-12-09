package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.UserAccount;
import org.luncert.facedetect.model.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepo extends CrudRepository<UserInfo, UserAccount> {

    UserInfo findByAccount(UserAccount account);
}