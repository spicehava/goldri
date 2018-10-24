package club.goldri.web.system.controller;

import club.goldri.web.system.domain.DatalogDiff;
import club.goldri.web.system.domain.SysDatalog;
import com.github.pagehelper.PageInfo;
import club.goldri.core.util.ResponseUtil;
import club.goldri.web.system.service.DatalogService;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api")
public class DatalogController {

    @Autowired
    private DatalogService datalogService;

    @GetMapping("/datalog")
    @RequiresPermissions("system:datalog:list")
    public ResponseEntity listAll(@ApiParam SysDatalog sysDatalog){
        List<SysDatalog> datalogList = this.datalogService.listAll(sysDatalog);
        return ResponseUtil.success(new PageInfo<SysDatalog>(datalogList));
    }

    /**
     * 获取所有修订版本
     * @param tableName
     * @param dataId
     * @return
     */
    @GetMapping("/datalog/listVersion/{tableName}/{dataId}")
    public ResponseEntity listVersionNumber(@PathVariable String tableName, @PathVariable String dataId){
        List<SysDatalog> datalogList = this.datalogService.listVersionNumber(tableName, dataId);
        return ResponseUtil.success(datalogList);
    }

    /**
     * 根据前台传过来的版本进行比对
     * @param tableName
     * @param dataId
     * @param version1
     * @param version2
     * @return
     */
    @GetMapping("/datalog/getDiffDataVersion/{tableName}/{dataId}/{version1}/{version2}")
    @RequiresPermissions("system:datalog:diff")
    public ResponseEntity diffDataVersion(@PathVariable String tableName,
                                          @PathVariable String dataId,
                                          @PathVariable String version1,
                                          @PathVariable String version2){
        List<DatalogDiff> diffList = this.datalogService.diffDataVersion(tableName, dataId, version1, version2);
        return ResponseUtil.success(diffList);
    }

    /**
     * 直接获取该业务数据最后修改的两次差异
     * @param tableName
     * @param dataId
     * @return
     */
    @GetMapping("/datalog/getDiffLastVersion/{tableName}/{dataId}")
    @RequiresPermissions("system:datalog:diff")
    public ResponseEntity diffLastVersion(@PathVariable String tableName, @PathVariable String dataId){
        List<DatalogDiff> diffList = this.datalogService.diffLastVersion(tableName, dataId);
        return ResponseUtil.success(diffList);
    }
}