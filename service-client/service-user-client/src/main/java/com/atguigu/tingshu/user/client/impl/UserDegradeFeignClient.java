package com.atguigu.tingshu.user.client.impl;


import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserDegradeFeignClient implements UserFeignClient {

    @Override
    public Result<UserInfoVo> getUserInfoVoByUserId(Long userId) {
        log.warn("根据用户ID查询用户信息失败");
        return Result.fail();
    }

    @Override
    public Result<Map<Long, Integer>> userIsPaidTrackList(Long userId, Long albumId, List<Long> trackIdList) {
        log.warn("根据用户ID查询用户是否付费的曲目列表失败");
        return Result.fail();
    }
}
