package club.goldri.web.system.service;


import club.goldri.core.common.service.BaseService;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.domain.SysOrg;

import java.util.List;

public interface OrgService extends BaseService<SysOrg> {

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysOrg
     * @return
     */
    List<SysOrg> listAll(SysOrg sysOrg);

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param sysOrg
     * @return
     */
    SysOrg commonSave(SysUser currentUser, SysOrg sysOrg);

    /**
     * 查询单个组织（根据需要封装子记录）
     *
     * @param id
     * @return
     */
    SysOrg selectOrgById(String id);

    /**
     * 软删除单个组织（清除缓存）
     * @param currentUser
     * @param id
     * @return
     */
    SysOrg removeOrgById(SysUser currentUser , String id) ;
}
