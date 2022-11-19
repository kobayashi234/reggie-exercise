package com.demo.reggie.interceptor;

import com.alibaba.fastjson2.JSON;
import com.demo.reggie.common.R;
import com.demo.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("拦截的请求：" + requestURI);
        //用户已登录，放行
        if(request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            return true;
        }
        if(request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            return true;
        }
        //未登录，返回json数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }
}
