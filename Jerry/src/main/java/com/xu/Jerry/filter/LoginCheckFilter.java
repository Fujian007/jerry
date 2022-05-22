package com.xu.Jerry.filter;
import com.alibaba.fastjson.JSON;
import com.xu.Jerry.common.BaseContext;
import com.xu.Jerry.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器
 * 检测是否登录
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")//拦截所有
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1，获取请求路径
        String requestURI = request.getRequestURI();
        //2，设置放行uri，如登录，退出，注册,静态页面等
        log.info("拦截到请求:{}",requestURI);
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login"    //移动端登录
        };

        //3，查看是否需要放行
        boolean check = checkURI(uris, requestURI);
        if (check){
            //如果支持放行
            log.info("本次请求无需处理:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1，判断后台管理人员是否已经登录了，已经登录了需要放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户:{} 已登陆",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            //把已经登录的员工id储存到副本线程的空间中
            //调用编写的BaseContext类
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //4-2，判断移动端是否已经登录了，已经登录了需要放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户:{} 已登陆",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            //把已经登录的员工id储存到副本线程的空间中
            //调用编写的BaseContext类
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //5，如果未登录，需要跳转到登录页面
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    /**
     * 检测uri是否放行
     * @param uris
     * @param requestURI
     * @return
     */
    public boolean checkURI(String[] uris,String requestURI){
        for (String s : uris) {
            //匹配成功返回true
            boolean match = PATH_MATCHER.match(s, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
