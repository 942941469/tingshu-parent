package com.atguigu.tingshu.user.api;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.tingshu.common.login.GuiGuLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.user.service.UserInfoService;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class UserInfoApiController {

	@Autowired
	private UserInfoService userInfoService;

	@GetMapping("/wxLogin/getUserInfo")
	@GuiGuLogin
	@Operation(summary = "获取用户信息")
	public Result<UserInfoVo> getUserInfo() {
		Long userId = AuthContextHolder.getUserId();
		return Result.ok(BeanUtil.copyProperties(userInfoService.getById(userId), UserInfoVo.class));
	}

	@GetMapping("/userInfo/getUserInfoVo/{userId}")
	@Operation(summary = "根据用户ID查询用户信息")
	public Result<UserInfoVo> getUserInfoVoByUserId(@PathVariable Long userId){
		return Result.ok(BeanUtil.copyProperties(userInfoService.getById(userId), UserInfoVo.class));
	}
}

