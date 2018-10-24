package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    //根据用户id查询用户角色
    List<SysRole> listRoleByUserId(String userId);
}