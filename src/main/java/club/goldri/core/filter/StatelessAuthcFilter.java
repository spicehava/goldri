package club.goldri.core.filter;

import club.goldri.core.constant.Constant;
import club.goldri.core.shiro.StatelessToken;
import club.goldri.core.constant.AuthConstant;
import club.goldri.core.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthcFilter extends AccessControlFilter {

    private static final Logger logger = LoggerFactory.getLogger(StatelessAuthcFilter.class);

    //需要过滤拦截的请求地址，多个地址用","分割
    @Value("${system.config.filterExclude}")
    private String excludeUrl;

    /**
     * 默认需要放行的请求地址
     */
    private String[] defalutExcludeUrl = new String[] {
            "/websocket","/logout","/login","/formLogin",".jpg",".png",".gif",".css",".js",".jpeg"
    };

    /**
     * CROS复杂请求时会先发送一个OPTIONS请求，来测试服务器是否支持本次请求，这个请求时不带数据的，请求成功后才会发送真实的请求。
     * 所以第一次发送请求要确认服务器支不支持接收这个header。第二次才会传数据，所以我们要做的就是把所有的OPTIONS请求统统放行
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setHeader("Access-control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
            httpResponse.setHeader("Access-Control-Expose-Headers", "content-type" + ", " + Constant.EXPOSE_HEADER);
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 先执行：isAccessAllowed 再执行onAccessDenied
     *
     * isAccessAllowed：表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，
     * 如果允许访问返回true，否则false；
     *
     * 如果返回true的话，就直接返回交给下一个filter进行处理。
     * 如果返回false的话，回往下执行onAccessDenied
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        //处理需要过滤的请求
        return this.include((HttpServletRequest)request);
    }

    /**
     * onAccessDenied：表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；
     * 如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String token = httpRequest.getHeader(AuthConstant.DEFAULT_TOKEN_NAME);
        if (token != null &&
                token.startsWith(AuthConstant.TOKEN_HEADER_PREFIX)){
                //截取token得到jwt格式的token信息
                String authToken = token.substring(AuthConstant.TOKEN_HEADER_PREFIX.length() + 1);
                if (JwtTokenUtil.validateToken(authToken, AuthConstant.SECRETKET)){
                    //1、客户端传入的用户身份
                    String username = httpRequest.getHeader(AuthConstant.CLIENT_PARAM_USERNAME);

                    //2、生成无状态Token
                    StatelessToken statelessToken = new StatelessToken(username, authToken);

                    try {
                        //3、委托给Realm进行登录
                        super.getSubject(request, response).login(statelessToken);
                    } catch (Exception e) {
                        //4、登录失败
                        onLoginFail(response, "认证失败，请重试或者重新登录！");
                        return false;
                    }
                    return true;
                }
        }
        this.onLoginFail(response, "无权访问！");

        return false;
    }

    /**
     * 检查当前url是否在排除url中
     * @param request
     * @return
     */
    public boolean include(HttpServletRequest request) {
        String u = request.getRequestURI();
        for (String e : defalutExcludeUrl) {
            if (u.endsWith(e)) {
                return true;
            }
        }

        if(StringUtils.isNotBlank(excludeUrl)){
            String[] customExcludes = excludeUrl.split(",");
            for (String e : customExcludes) {
                if(e.contains("*")){
                    e = e.replace("*","");
                    if(u.startsWith(e)){
                        return true;
                    }
                }
                if (u.endsWith(e)) {
                    return true;
                }
            }
        }

        return false;
    }

    //登录失败时默认返回401状态码
    private void onLoginFail(ServletResponse response, String msg) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setHeader(Constant.HEADER_ERROR, java.net.URLEncoder.encode(msg, "utf-8"));
    }
}
