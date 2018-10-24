package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysUser;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据检索条件获取所有用户
     * @param sysUser
     * @return
     */
    List<SysUser> listAll(SysUser sysUser);

    /**
     * 根据roleId获取用户
     * @param roleId
     * @return
     */
    List<SysUser> listUserByRoleId(String roleId);
}