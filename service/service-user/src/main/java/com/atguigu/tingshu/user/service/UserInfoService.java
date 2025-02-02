package com.atguigu.tingshu.user.service;

import com.atguigu.tingshu.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    Map<String, String> wxLogin(String code);

    Map<Long, Integer> userIsPaidTrackList(Long userId, Long albumId, List<Long> trackIdList);
}
