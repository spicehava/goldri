package club.goldri.web.system.service;

import club.goldri.core.common.service.BaseService;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.domain.SysDict;

import java.util.List;

public interface DictService extends BaseService<SysDict> {

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysDict
     * @return
     */
    List<SysDict> listAll(SysDict sysDict);

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param sysDict
     * @return
     */
    SysDict commonSave(SysUser currentUser, SysDict sysDict);

    /**
     * 查询单个字典（根据需要封装子记录）
     *
     * @param type
     * @param code
     * @return
     */
    SysDict selectDictByTypeAndCode(String type, String  code);

    /**
     * 软删除单个字典（清除缓存）
     * @param currentUser
     * @param type
     * @param code
     * @return
     */
    SysDict removeDictByTypeAndCode(SysUser currentUser, String type, String code) ;

    /**
     * 根据type查询
     * @param type
     * @return
     */
    List<SysDict> listByType(String type);

}
