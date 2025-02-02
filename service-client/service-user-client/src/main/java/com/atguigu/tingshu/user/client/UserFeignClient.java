package com.atguigu.tingshu.user.client;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.user.client.impl.UserDegradeFeignClient;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品列表API接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-user",path = "api/user", fallback = UserDegradeFeignClient.class)
public interface UserFeignClient {
    @GetMapping("/userInfo/getUserInfoVo/{userId}")
    Result<UserInfoVo> getUserInfoVoByUserId(@PathVariable Long userId);

    @PostMapping("/userInfo/userIsPaidTrack/{userId}/{albumId}")
    Result<Map<Long, Integer>> userIsPaidTrackList(@PathVariable Long userId, @PathVariable Long albumId, @RequestBody List<Long> trackIdList);
}
