package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysResource;

import java.util.List;

public interface SysResourceMapper extends BaseMapper<SysResource> {
    //根据用户id获取用户资源
    List<SysResource> listResourceByUserId(String userId);

    //根据角色id获取角色资源
    List<SysResource> listResourceByRoleId(String roleId);

    //根据资源id查询父层级id（包括当前查询的id)
    List<String> listResParentIdUp(List<String> resourceIdList);

    //根据roleId获取所有资源，并标识该资源是否checked
    List<SysResource> listAllResourceByRoleId(String roleId);
}