package club.goldri.web.system.controller;

import club.goldri.core.util.StringUtil;
import com.github.pagehelper.PageInfo;
import club.goldri.core.util.BeanUtil;
import club.goldri.core.util.ResponseUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysDict;
import club.goldri.web.system.service.DictService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 查询方法
     * @param sysDict
     * @return
     */
    @GetMapping("/dicts")
    @RequiresPermissions("system:dict:list")
    public ResponseEntity listAll(@ApiParam SysDict sysDict){
        List<SysDict> dictList = this.dictService.listAll(sysDict);
        return ResponseUtil.success(new PageInfo<SysDict>(dictList));
    }

    /**
     * 添加单个字典
     * @param request
     * @param sysDict
     * @return
     */
    @PostMapping("/dicts")
    @RequiresPermissions("system:dict:add")
    public ResponseEntity save(HttpServletRequest request,
                               @ApiParam @RequestBody SysDict sysDict){
        if(StringUtil.isEmpty(sysDict.getType()) || StringUtil.isEmpty(sysDict.getCode())){
            return ResponseUtil.failure("参数错误！");
        }

        //设置创建信息
        BeanUtil.setCreateUser(request, sysDict);

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysDict);

        this.dictService.commonSave(SystemCacheUtil.getUserByRequest(request), sysDict);
        return ResponseUtil.success(sysDict);
    }

    /**
     * 查看单个字典
     *
     * @param type
     * @param code
     * @return
     */
    @GetMapping("/dicts/{type}/{code}")
    @RequiresPermissions("system:dict:view")
    public ResponseEntity viewDict(@PathVariable(required = true) String type,
                             @PathVariable(required = true) String code){
        SysDict dict = new SysDict();
        dict.setType(type);
        dict.setCode(code);

        dict = this.dictService.selectDictByTypeAndCode(type, code);

        return ResponseUtil.success(dict);
    }

    /**
     * 编辑单个字典
     * @param request
     * @param sysDict
     * @return
     */
    @PutMapping("/dicts")
    @RequiresPermissions("system:dict:edit")
    public ResponseEntity updateDict(HttpServletRequest request,
                                 @ApiParam @RequestBody SysDict sysDict){
        if(StringUtil.isEmpty(sysDict.getType()) || StringUtil.isEmpty(sysDict.getCode())){
            return ResponseUtil.failure("参数错误！");
        }

        //设置更新信息
        BeanUtil.setUpdateUser(request, sysDict);

        this.dictService.commonSave(SystemCacheUtil.getUserByRequest(request), sysDict);
        return ResponseUtil.success(sysDict);
    }

    /**
     * 软删除单个字典（根据id删除）
     * @param type
     * @param code
     * @return
     */
    @DeleteMapping("/dicts")
    @RequiresPermissions("system:dict:remove")
    public ResponseEntity removeDict(HttpServletRequest request,
                                     @PathVariable(required = true) String type,
                                     @PathVariable(required = true) String code){
        SysDict result = this.dictService
                .removeDictByTypeAndCode(SystemCacheUtil
                        .getUserByRequest(request), type, code);

        if(result != null){
            return ResponseUtil.failure("删除失败！");
        }

        return ResponseUtil.success();
    }

    /**
     * 根据类型查询
     *
     * @param type
     * @return
     */
    @GetMapping("/dicts/{type}")
    public ResponseEntity listByType(@PathVariable(required = true) String type){
        List<SysDict> dictList = this.dictService.listByType(type);

        return ResponseUtil.success(dictList);
    }
}
