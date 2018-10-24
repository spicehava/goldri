package club.goldri.core.filter;

import club.goldri.core.constant.Constant;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CrosFilter : 跨域资源共享过滤器, 该过滤器设置response header, 解决跨域ajax请求报错
 */
@Component
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse)res;

        response.setHeader("Access-Control-Allow-Origin", "*");// 允许所有域进行访问,可以指定多个Access-Control-Allow-Origin:http://localhost:8080/
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");// 允许的方法
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control, Authorization, username");
        response.setHeader("Access-Control-Expose-Headers", "content-type" + ", " + Constant.EXPOSE_HEADER);//如果不设置，js获取不到头信息
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
