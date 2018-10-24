package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysDict;
import org.apache.ibatis.annotations.Select;

public interface SysDictMapper extends BaseMapper<SysDict> {
    @Select("SELECT MAX(sd.sort) FROM sys_dict sd WHERE sd.del_flag = 'NORMAL' AND sd.type = #{type}")
    Integer selectMaxSort(String type);
}