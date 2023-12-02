package com.yupi.usercenter.common.Interceptor;

import com.alibaba.fastjson2.JSON;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.yupi.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author: wy
 * @CreateTime: 2023-12-02  19:30
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info(requestURI);
        if (requestURI.contains("/user/login") || requestURI.contains("/user/register")) {
            return true;
        }
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            response.setStatus(500);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getCharacterEncoding();
            String jsonString = JSON.toJSONString(ResultUtils.error(5000, "未登录", null));
            response.getOutputStream().write(jsonString.getBytes());
            return false;
        }
        return true;
    }
}
