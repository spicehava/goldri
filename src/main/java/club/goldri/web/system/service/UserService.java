package club.goldri.web.system.service;

import club.goldri.core.common.service.BaseService;
import club.goldri.web.system.domain.SysUser;

import java.util.List;

public interface UserService extends BaseService<SysUser> {

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysUser
     * @return
     */
    List<SysUser> listAll(SysUser sysUser);

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param sysUser
     * @return
     */
    SysUser commonSave(SysUser currentUser, SysUser sysUser);

    /**
     * 根据用户名获取用户信息（封装部门、角色）
     * @param username
     * @return
     */
    SysUser selectUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息（封装部门、角色）
     *
     * @param userId
     * @return
     */
    SysUser selectUserById(String userId);

    /**
     * 软删除用户（清除缓存）
     * @param currentUser
     * @param userId
     * @return
     */
    SysUser removeUserById(SysUser currentUser , String userId) ;

    /**
     * 根据roleId获取用户
     * @param roleId
     * @return
     */
    List<SysUser> listUserByRoleId(String roleId);
}
