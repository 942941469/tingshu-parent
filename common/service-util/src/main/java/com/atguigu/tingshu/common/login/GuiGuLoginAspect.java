package com.atguigu.tingshu.common.login;

import com.atguigu.tingshu.common.constant.RedisConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.result.ResultCodeEnum;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author admin
 * @version 1.0
 */

@Component
@Aspect
public class GuiGuLoginAspect {

    @Autowired
    private RedisTemplate redisTemplate;
    @Around("@annotation(guiGuLogin)")
    public Object login(ProceedingJoinPoint joinPoint, GuiGuLogin guiGuLogin) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        // 未登录直接抛出异常
        if (guiGuLogin.required() && StringUtils.isEmpty(token)) {
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        // 不需要登录如果有token也获取
        if (StringUtils.isEmpty(token)) {
            UserInfoVo userInfoVo = (UserInfoVo) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
            if (userInfoVo != null) {
                AuthContextHolder.setUserId(userInfoVo.getId());
                AuthContextHolder.setUsername(userInfoVo.getNickname());
            }
        }
        return joinPoint.proceed();
    }
}
