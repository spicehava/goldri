package club.goldri.web.system.controller;

import club.goldri.core.util.*;
import club.goldri.core.common.exception.ForbiddenException;
import club.goldri.core.constant.AuthConstant;
import club.goldri.core.constant.Constant;
import club.goldri.core.util.*;
import club.goldri.web.system.domain.SysResource;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.ResourceService;
import club.goldri.web.system.service.UserService;
import club.goldri.web.system.util.SysLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;
    /**
     * @Description 用户登录
     * @param user
     * @param response
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody SysUser user,HttpServletRequest request, HttpServletResponse response) {
        if (StringUtil.isEmpty(user.getUsername()) || StringUtil.isEmpty(user.getPassword())) {
            throw new ForbiddenException("用户名或密码不可为空！");
        }
        logger.debug(user.getUsername() + "登录请求");
        String errorMsg = "";
        SysUser sysUser = this.userService.selectUserByUsername(user.getUsername());
        if (sysUser == null) {
            errorMsg = "用户不存在！";
        } else if (Constant.USER_STATE_LOCK.equals(sysUser.getState())) {
            errorMsg = "用户被锁定！";
        }

        if(StringUtil.isNotEmpty(errorMsg)){
            SysLogUtil.saveLog(request, sysUser, user.getUsername() + "："+ errorMsg,"系统登录" );
            throw new ForbiddenException(errorMsg);
        }

        //验证密码是否正确
        String password = PasswordUtil.encryptPassword(user.getPassword(),user.getUsername() + sysUser.getSalt());

        if(password.equals(sysUser.getPassword())){
            String accessToken = JwtTokenUtil.createToken(user.getUsername(),AuthConstant.TTLMILLS, AuthConstant.SECRETKET );
            response.setHeader(AuthConstant.DEFAULT_TOKEN_NAME, accessToken);
            response.setHeader(AuthConstant.CLIENT_PARAM_USERNAME,  sysUser.getUsername());
            Map<String, String> authMap = new HashMap<String, String>();
            authMap.put(AuthConstant.DEFAULT_TOKEN_NAME, accessToken);
            authMap.put(AuthConstant.CLIENT_PARAM_USERNAME,  sysUser.getUsername());
            authMap.put(AuthConstant.CLIENT_PARAM_USERID, sysUser.getId());
            SysLogUtil.saveLog(request, sysUser, "系统登录");
            return ResponseEntity.ok().body(authMap);
        } else {
            SysLogUtil.saveLog(request, sysUser, user.getUsername() + "：密码错误！","系统登录" );
            throw new ForbiddenException("密码错误！");
        }
    }

    /**
     * 获取当前人的菜单
     * @param request
     * @return
     */
    @GetMapping("/listMenu")
    public ResponseEntity listMenu(HttpServletRequest request) {
        SysUser sysUser = SystemCacheUtil.getUserByRequest(request);
        List<SysResource> resourceList = this.resourceService.listMenuTreeByUserId(sysUser.getId());

        return ResponseUtil.success(resourceList);
    }

    /**
     * 获取当前人的权限
     * @param request
     * @return
     */
    @GetMapping("/listPermission")
    public ResponseEntity listPermissions(HttpServletRequest request) {
        SysUser sysUser = SystemCacheUtil.getUserByRequest(request);
        List<SysResource> resourceList = this.resourceService.listPermissionByUserId(sysUser.getId());

        return ResponseUtil.success(resourceList);
    }

    /**
     * @description 登出处理
     */
    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        logger.debug("Logout Success...");

        SysUser sysUser = SystemCacheUtil.getUserByRequest(request);
        SysLogUtil.saveLog(request, sysUser, "系统退出");
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/cleanCache")
    @Caching(evict = {
            @CacheEvict(value = SystemCacheUtil.CACHE_RESOURCE, allEntries = true),
            @CacheEvict(value = SystemCacheUtil.CACHE_USER, allEntries = true),
            @CacheEvict(value = SystemCacheUtil.CACHE_ROLE, allEntries = true),
            @CacheEvict(value = SystemCacheUtil.CACHE_ORG, allEntries = true),
    })
    public void cleanCache(){
        System.out.println("sss");
    }
}