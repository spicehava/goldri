package club.goldri.web.system.controller;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.*;
import club.goldri.web.system.domain.SysUser;
import com.github.pagehelper.PageInfo;
import club.goldri.core.util.*;
import club.goldri.web.system.service.UserService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Value("${system.user.oriPwd}")
    private String oriPwd;

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     * @param sysUser
     * @return
     */
    @GetMapping("/users")
    @RequiresPermissions("system:user:list")
    public ResponseEntity listAll(@ApiParam SysUser sysUser){
        List<SysUser> userList = this.userService.listAll(sysUser);
        return ResponseUtil.success(new PageInfo<SysUser>(userList));
    }

    @PostMapping("/users")
    @RequiresPermissions("system:user:add")
    public ResponseEntity save(HttpServletRequest request,
                                    @ApiParam @RequestBody SysUser sysUser){
        if(sysUser.getId() != null){
            return ResponseUtil.failure("参数错误！");
        }

        if(sysUser.getPassword() == null || "".equals(sysUser.getPassword())){
            //给出默认密码
            sysUser.setPassword(this.oriPwd);
        }

        //随机盐
        if(sysUser.getSalt() == null || "".equals(sysUser.getPassword())){
            sysUser.setSalt(IdGenUtil.uuid().substring(0,10));
        }

        //初始化用户状态
        if(sysUser.getState() == null || "".equals(sysUser.getState())){
            sysUser.setState(Constant.USER_STATE_NORMAL);
        }

        //为用户加密
        PasswordUtil.encryptPassword(sysUser);

        //设置创建信息
        BeanUtil.setCreateUser(request, sysUser);

        //验证用户名合法性
        if(!this.validateUsername(sysUser.getUsername())){
            return ResponseUtil.failure("该用户名已被占用！", sysUser);
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysUser);

        sysUser = this.userService.commonSave(SystemCacheUtil.getUserByRequest(request), sysUser);

        return ResponseUtil.success(sysUser);
    }

    /**
     * 验证用户名是否存在
     * 存在返回false，不存在返回true
     * @param username
     * @return
     */
    @GetMapping("/users/validate/{username}")
    public Boolean validateUsername(@PathVariable String username){

        SysUser user = this.userService.selectUserByUsername(username);
        if(user != null){
            return false;
        } else {
            return true;
        }
    }

    @GetMapping("/users/{id}")
    @RequiresPermissions("system:user:view")
    public ResponseEntity view(@ApiParam @PathVariable String id){
        SysUser sysUser = this.userService.selectUserById(id);
        return ResponseUtil.success(sysUser);
    }

    @PutMapping("/users")
    @RequiresPermissions("system:user:edit")
    public ResponseEntity update(HttpServletRequest request,
                                   @RequestBody SysUser sysUser){
        if(sysUser.getId() == null || "".equals(sysUser.getId())){
            return ResponseUtil.failure("参数错误！");
        }

        SysUser user = this.userService.selectUserById(sysUser.getId());

        if(user == null){
            return ResponseUtil.failure("未找到该用户！");
        }

        sysUser.setUsername(user.getUsername());//不可修改用户名

        if(StringUtil.isNotEmpty(sysUser.getPassword())){
            sysUser.setSalt(user.getSalt());

            PasswordUtil.encryptPassword(sysUser);
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysUser);

        sysUser = this.userService.commonSave(SystemCacheUtil.getUserByRequest(request), sysUser);

        return ResponseUtil.success(sysUser);
    }

    /**
     * 用于修改本人信息，不需要权限码
     * @param request
     * @param sysUser
     * @return
     */
    @PostMapping("/users/self")
    public ResponseEntity updateUserSelf(HttpServletRequest request,
                                      @RequestBody SysUser sysUser){
        /*验证是否当前人*/
        if(sysUser.getId() == null || "".equals(sysUser.getId())){
            return ResponseUtil.failure("参数错误！");
        }

        SysUser user = this.userService.selectUserById(sysUser.getId());

        if(user == null){
            return ResponseUtil.failure("未找到该用户！");
        }

        if(!user.getUsername().equals(request.getHeader("username"))){
            return ResponseUtil.failure("只能修改本人的信息！");
        }

        sysUser.setUsername(user.getUsername());//不可修改用户名
        if(StringUtil.isNotEmpty(sysUser.getPassword())){
            sysUser.setSalt(user.getSalt());

            PasswordUtil.encryptPassword(sysUser);
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysUser);

        sysUser = this.userService.commonSave(SystemCacheUtil.getUserByRequest(request), sysUser);

        return ResponseUtil.success(sysUser);
    }

    @DeleteMapping("/users/{id}")
    @RequiresPermissions("system:user:remove")
    public ResponseEntity remove(HttpServletRequest request, @PathVariable String id){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        SysUser res = this.userService.removeUserById(currentUser, id);

        if(res != null){
           return ResponseUtil.failure("删除失败！");
        }

        return ResponseUtil.success();
    }

    /**
     * 仅用于当前人修改自己的密码
     * @param request
     * @param sysUser
     * @return
     */
    @PostMapping("/users/pwd")
    public ResponseEntity modifyPwd(HttpServletRequest request,@RequestBody SysUser sysUser){

        if(sysUser.getUsername() == null || "".equals(sysUser.getUsername())){
            return ResponseUtil.failure("用户名不能为空！");
        }

        if(!sysUser.getUsername().equals(request.getHeader("username"))){
            return ResponseUtil.failure("只能修改本人的密码！");
        }

        if(sysUser.getOriPassword() == null || "".equals(sysUser.getOriPassword())){
            return ResponseUtil.failure("原密码不能为空！");
        }

        if(sysUser.getPassword() == null || "".equals(sysUser.getPassword())){
            return ResponseUtil.failure("修改后密码不能为空！");
        }

        //验证原始密码合法性
        SysUser user = this.userService.selectUserByUsername(sysUser.getUsername());
        String oriPassword = PasswordUtil.encryptPassword(sysUser.getOriPassword(),sysUser.getUsername() + user.getSalt());

        if(!oriPassword.equals(user.getPassword())){
            return ResponseUtil.failure("输入的原始密码不正确！");
        }

        //更新密码
        String newPwd = PasswordUtil.encryptPassword(sysUser.getPassword(),user.getUsername() + user.getSalt());
        SysUser modifiedUser = new SysUser();
        modifiedUser.setId(user.getId());
        modifiedUser.setUsername(user.getUsername());
        modifiedUser.setPassword(newPwd);

        //设置更新信息
        BeanUtil.setUpdateUser(request, modifiedUser);

        modifiedUser = this.userService.commonSave(SystemCacheUtil.getUserByRequest(request), modifiedUser);

        return ResponseUtil.success(modifiedUser);
    }

    @PostMapping("/users/restPwd")
    @RequiresPermissions("system:user:restPwd")
    public ResponseEntity restPwd(HttpServletRequest request,@RequestBody SysUser sysUser){

        if(sysUser.getUsername() == null || "".equals(sysUser.getUsername())){
            return ResponseUtil.failure("用户名不能为空！");
        }

        if(sysUser.getPassword() == null || "".equals(sysUser.getPassword())){
            sysUser.setPassword(this.oriPwd);
        }

        //验证用户合法性
        SysUser user = this.userService.selectUserByUsername(sysUser.getUsername());
        if(user == null){
            return ResponseUtil.failure("未找到用户！");
        }

        //更新密码
        String newPwd = PasswordUtil.encryptPassword(sysUser.getPassword(),user.getUsername() + user.getSalt());
        SysUser modifiedUser = new SysUser();
        modifiedUser.setId(user.getId());
        modifiedUser.setUsername(user.getUsername());
        modifiedUser.setPassword(newPwd);

        //设置更新信息
        BeanUtil.setUpdateUser(request, modifiedUser);

        modifiedUser = this.userService.commonSave(SystemCacheUtil.getUserByRequest(request), modifiedUser);

        return ResponseUtil.success(modifiedUser);
    }
}
