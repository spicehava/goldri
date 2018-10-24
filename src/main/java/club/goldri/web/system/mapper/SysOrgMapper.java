package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysOrg;

import java.util.List;

public interface SysOrgMapper extends BaseMapper<SysOrg> {

    //根据用户id查询用户所属组织
    List<SysOrg> listOrgByUserId(String userId);
}