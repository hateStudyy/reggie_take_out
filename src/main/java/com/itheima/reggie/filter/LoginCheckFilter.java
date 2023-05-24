package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 * @author coldwind
 * @version 1.0
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request1 = (HttpServletRequest) request;
        HttpServletResponse response1 = (HttpServletResponse) response;

        log.info("拦截到请求: {}", request1.getRequestURI());
        //1.获得URI
        String requestURI = request1.getRequestURI();//

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2.判断本次请求是否需要处理
        boolean check = check(requestURI, urls);

        //3.如果不需要处理，放行
        if(check){
            log.info("本次{}请求不需要处理",requestURI);
            chain.doFilter(request, response);
            return;
        }

        //4-1.判断登录状态，如果已登录，直接放行
        if(request1.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request1.getSession().getAttribute("employee"));

            Long empId =(Long) request1.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            chain.doFilter(request, response);
            return;
        }

        //4-2.判断登录状态，如果已登录，直接放行
        if(request1.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request1.getSession().getAttribute("user"));

            Long userId =(Long) request1.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            chain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录则返回未登录结果，通过输出流向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 匹配路径，判断此次请求是否放行
     * @param requestURI
     * @return
     */
    public boolean check(String requestURI, String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) return true;
        }
        return false;
    }
}
