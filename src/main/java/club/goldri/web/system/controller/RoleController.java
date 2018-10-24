package club.goldri.web.system.controller;

import club.goldri.core.util.StringUtil;
import com.github.pagehelper.PageInfo;
import club.goldri.core.util.BeanUtil;
import club.goldri.core.util.ResponseUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysResource;
import club.goldri.web.system.domain.SysRole;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.ResourceService;
import club.goldri.web.system.service.RoleService;
import club.goldri.web.system.service.UserService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    /**
     * 查询方法
     * @param sysRole
     * @return
     */
    @GetMapping("/roles")
    @RequiresPermissions("system:role:list")
    public ResponseEntity listAll(SysRole sysRole){
        List<SysRole> roleList = this.roleService.listAll(sysRole);
        return ResponseUtil.success(new PageInfo<SysRole>(roleList));
    }

    /**
     * 添加单个实体（id必须为空）
     * @param request
     * @param sysRole
     * @return
     */
    @PostMapping("/roles")
    @RequiresPermissions("system:role:add")
    public ResponseEntity save(HttpServletRequest request,
                               @ApiParam @RequestBody SysRole sysRole){
        if(sysRole.getId() != null){
            return ResponseUtil.failure("参数错误！");
        }

        //设置创建信息
        BeanUtil.setCreateUser(request, sysRole);

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysRole);

        this.roleService.commonSave(SystemCacheUtil.getUserByRequest(request), sysRole);

        return ResponseUtil.success(sysRole);
    }

    /**
     * 查看单个实体
     * @param id
     * @return
     */
    @GetMapping("/roles/{id}")
    @RequiresPermissions("system:role:view")
    public ResponseEntity view(@PathVariable String id){
        SysRole sysRole = this.roleService.selectRoleById(id);
        return ResponseUtil.success(sysRole);
    }

    /**
     * 编辑单个实体（id不可为空）
     * @param request
     * @param sysRole
     * @return
     */
    @PutMapping("/roles")
    @RequiresPermissions("system:role:edit")
    public ResponseEntity updateRole(HttpServletRequest request,
                                     @ApiParam @RequestBody SysRole sysRole){
        //设置更新信息
        BeanUtil.setUpdateUser(request, sysRole);

        this.roleService.commonSave(SystemCacheUtil.getUserByRequest(request), sysRole);

        return ResponseUtil.success(sysRole);
    }

    /**
     * 软删除单个实体（根据id删除）
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("/roles/{id}")
    @RequiresPermissions("system:role:remove")
    public ResponseEntity remove(HttpServletRequest request, @PathVariable String id){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        SysRole result = this.roleService.removeRoleById(currentUser, id);

        if(result != null){
            return ResponseUtil.failure("删除失败！");
        }

        return ResponseUtil.success();
    }

    /**
     * 获取某个角色的资源
     * @param id
     * @return
     */
    @GetMapping("/roles/resource/{id}")
    public ResponseEntity listRoleResource(@PathVariable String id){
        List<SysResource> listResource = this.resourceService.listResourceByRoleId(id);
        return ResponseUtil.success(listResource);
    }

    /**
     * 给角色设置资源
     * @param request
     * @param sysRole
     * @return
     */
    @PostMapping("/roles/setResources")
    @RequiresPermissions("system:role:setResource")
    public ResponseEntity setResource(HttpServletRequest request, @RequestBody SysRole sysRole){
        if(StringUtil.isEmpty(sysRole.getId())){
            return ResponseUtil.failure("参数错误！");
        }
        this.roleService.setResource(SystemCacheUtil.getUserByRequest(request), sysRole.getResourceIdList(),sysRole.getId());
        return ResponseUtil.success();
    }

    /**
     * 为角色设置用户
     *
     * @param request
     * @param sysRole
     * @return
     */
    @PostMapping("/roles/setUser")
    @RequiresPermissions("system:role:setUser")
    public ResponseEntity setUser(HttpServletRequest request, @RequestBody SysRole sysRole){
        if(StringUtil.isEmpty(sysRole.getId())){
            return ResponseUtil.failure("参数错误！");
        }
        this.roleService.setUser(SystemCacheUtil.getUserByRequest(request), sysRole.getUserIdList(), sysRole.getId());
        return ResponseUtil.success();
    }

    /**
     * 根据角色获取用户列表
     * @param roleId
     * @return
     */
    @GetMapping("/roles/listuser/{roleId}")
    public ResponseEntity listuser(@ApiParam @PathVariable String roleId){
        List<SysUser> listUser = this.userService.listUserByRoleId(roleId);
        return ResponseUtil.success(listUser);
    }

    /**
     *根据roleId获取所有资源，标识是否选中状态
     * @param roleId
     * @return
     */
    @GetMapping("/roles/listRes/{roleId}")
    public ResponseEntity listResourceByRoleId(@PathVariable String roleId){
        List<SysResource> listResource = this.resourceService.listAllResourceByRoleId(roleId);
        return ResponseUtil.success(listResource);
    }
}
