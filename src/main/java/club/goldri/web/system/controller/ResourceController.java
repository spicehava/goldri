package club.goldri.web.system.controller;

import com.github.pagehelper.PageInfo;
import club.goldri.core.util.BeanUtil;
import club.goldri.core.util.ResponseUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysResource;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.ResourceService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 查询方法
     * @param sysResource
     * @return
     */
    @GetMapping("/resources")
    @RequiresPermissions("system:resource:list")
    public ResponseEntity listAll(@ApiParam SysResource sysResource){
        List<SysResource> resourceList = this.resourceService.listAll(sysResource);
        return ResponseUtil.success(new PageInfo<SysResource>(resourceList));
    }

    /**
     * 添加单个资源（id必须为空）
     * @param request
     * @param sysResource
     * @return
     */
    @PostMapping("/resources")
    @RequiresPermissions("system:resource:add")
    public ResponseEntity save(HttpServletRequest request,
                                       @ApiParam @RequestBody SysResource sysResource){
        if(sysResource.getId() != null){
            return ResponseUtil.failure("参数错误！");
        }

        //设置创建信息
        BeanUtil.setCreateUser(request, sysResource);

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysResource);

        //TODO 暂定将levle设定为1，以后有需要再处理
        sysResource.setLevel(1);
        this.resourceService.commonSave(SystemCacheUtil.getUserByRequest(request), sysResource);

        return ResponseUtil.success(sysResource);
    }

    /**
     * 查看单个资源
     * @param id
     * @return
     */
    @GetMapping("/resources/{id}")
    @RequiresPermissions("system:resource:view")
    public ResponseEntity view(@ApiParam @PathVariable String id){
        SysResource sysResource = this.resourceService.getResourceById(id);
        return ResponseUtil.success(sysResource);
    }

    /**
     * 编辑单个资源（id不可为空）
     * @param request
     * @param sysResource
     * @return
     */
    @PutMapping("/resources")
    @RequiresPermissions("system:resource:edit")
    public ResponseEntity update(HttpServletRequest request,
                                 @ApiParam @RequestBody SysResource sysResource){
        if(sysResource.getId() == null || "".equals(sysResource.getId())){
            return ResponseUtil.failure("参数错误！");
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysResource);

        sysResource = this.resourceService.commonSave(SystemCacheUtil.getUserByRequest(request), sysResource);

        return ResponseUtil.success(sysResource);
    }

    /**
     * 软删除单个资源（根据id删除）
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("/resources")
    @RequiresPermissions("system:resource:remove")
    public ResponseEntity remove(HttpServletRequest request, @PathVariable String id){
        SysUser currentUser = SystemCacheUtil.getUserByRequest(request);

        SysResource result = this.resourceService.removeResourceById(currentUser, id);

        if(result != null){
            return ResponseUtil.failure("删除失败！");
        }

        return ResponseUtil.success();
    }
}
