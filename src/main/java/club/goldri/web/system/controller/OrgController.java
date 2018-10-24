package club.goldri.web.system.controller;

import club.goldri.core.util.BeanUtil;
import com.github.pagehelper.PageInfo;
import club.goldri.core.util.ResponseUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysOrg;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.OrgService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api")
public class OrgController {

    @Autowired
    private OrgService orgService;

    /**
     * 查询方法
     * @param sysOrg
     * @return
     */
    @GetMapping("/orgs")
    @RequiresPermissions("system:org:list")
    public ResponseEntity listAll(@ApiParam SysOrg sysOrg){
        List<SysOrg> orgList = this.orgService.listAll(sysOrg);
        return ResponseUtil.success(new PageInfo<SysOrg>(orgList));
    }

    /**
     * 添加单个组织（id必须为空）
     * @param request
     * @param sysOrg
     * @return
     */
    @PostMapping("/orgs")
    @RequiresPermissions("system:org:add")
    public ResponseEntity  save(HttpServletRequest request,
                                @ApiParam @RequestBody SysOrg sysOrg){
        if(sysOrg.getId() != null){
            return ResponseUtil.failure("参数错误！");
        }

        //TODO 其他参数验证

        //TODO 初始化参数

        //设置创建信息
        BeanUtil.setCreateUser(request, sysOrg);

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysOrg);

        sysOrg = this.orgService.commonSave(SystemCacheUtil.getUserByRequest(request), sysOrg);

        return ResponseUtil.success(sysOrg);
    }

    /**
     * 查看单个组织
     * @param id
     * @return
     */
    @GetMapping("/orgs/{id}")
    @RequiresPermissions("system:org:view")
    public ResponseEntity view(@ApiParam @PathVariable String id){
        SysOrg sysOrg = this.orgService.selectOrgById(id);
        return ResponseUtil.success(sysOrg);
    }

    /**
     * 编辑单个组织（id不可为空）
     * @param request
     * @param sysOrg
     * @return
     */
    @PutMapping("/orgs")
    @RequiresPermissions("system:org:edit")
    public ResponseEntity update(HttpServletRequest request,
                                 @ApiParam @RequestBody SysOrg sysOrg){
        if(sysOrg.getId() == null || "".equals(sysOrg.getId())){
            return ResponseUtil.failure("参数错误！");
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysOrg);

        sysOrg = this.orgService.commonSave(SystemCacheUtil.getUserByRequest(request), sysOrg);

        return ResponseUtil.success(sysOrg);
    }

    /**
     * 软删除单个组织（根据id删除）
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("/orgs/{id}")
    @RequiresPermissions("system:org:remove")
    public ResponseEntity remove(HttpServletRequest request, @PathVariable String id){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        SysOrg result = this.orgService.removeOrgById(currentUser, id);

        if(result != null){
            return ResponseUtil.failure("删除失败！");
        }

        return ResponseUtil.success();
    }
}