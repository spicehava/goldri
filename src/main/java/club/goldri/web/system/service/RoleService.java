package club.goldri.web.system.service;

import club.goldri.core.common.service.BaseService;
import club.goldri.web.system.domain.SysRole;
import club.goldri.web.system.domain.SysUser;

import java.util.List;

public interface RoleService extends BaseService<SysRole> {

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysRole
     * @return
     */
    List<SysRole> listAll(SysRole sysRole);

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param sysRole
     * @return
     */
    SysRole commonSave(SysUser currentUser, SysRole sysRole);

    /**
     * 查询单个角色（根据需要封装子记录）
     *
     * @param id
     * @return
     */
    SysRole selectRoleById(String id);

    /**
     * 软删除单个角色（清除缓存）
     * @param currentUser
     * @param id
     * @return
     */
    SysRole removeRoleById(SysUser currentUser ,String id) ;

    /**
     * 为角色设置资源
     * @param currentUser
     * @param resourceIdList
     * @param roleId
     */
    void setResource(SysUser currentUser, List<String> resourceIdList , String roleId);

    /**
     * 为角色设置用户
     * @param currentUser
     * @param userIdList
     * @param roleId
     */
    void setUser(SysUser currentUser, List<String> userIdList, String roleId);

}
