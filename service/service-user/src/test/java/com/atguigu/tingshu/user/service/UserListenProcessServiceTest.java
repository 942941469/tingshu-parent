package com.atguigu.tingshu.user.service;

import com.atguigu.tingshu.model.user.UserListenProcess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;

/**
 * @author admin
 * @version 1.0
 */

@SpringBootTest
public class UserListenProcessServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public void testCreateUser() {
        UserListenProcess userListenProcess = new UserListenProcess();
        userListenProcess.setUserId(17L);
        userListenProcess.setAlbumId(21L);
        userListenProcess.setTrackId(51923L);
        userListenProcess.setBreakSecond(BigDecimal.valueOf(39.12));
        userListenProcess.setIsShow(1);
        userListenProcess.setCreateTime(new java.util.Date());
        userListenProcess.setUpdateTime(new java.util.Date());
        mongoTemplate.insert(userListenProcess);
    }

}
