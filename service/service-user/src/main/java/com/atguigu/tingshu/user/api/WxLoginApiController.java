package com.atguigu.tingshu.user.api;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.tingshu.common.login.GuiGuLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.user.UserInfo;
import com.atguigu.tingshu.user.service.UserInfoService;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "微信授权登录接口")
@RestController
@RequestMapping("/api/user/wxLogin")
@Slf4j
public class WxLoginApiController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/wxLogin/{code}")
    @Operation(summary = "微信授权登录接口")
    public Result<Map<String, String>> wxLogin(@PathVariable String code) {
        return Result.ok(userInfoService.wxLogin(code));
    }

    @PostMapping("wxLogin/updateUser")
    @Operation(summary = "更新用户信息")
    @GuiGuLogin
    public Result updateUser(@RequestBody UserInfoVo userInfoVo) {
        userInfoService.updateById(BeanUtil.copyProperties(userInfoVo, UserInfo.class));
        return Result.ok();
    }
}
