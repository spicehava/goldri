package club.goldri.web.system.mapper;

import club.goldri.core.common.mapper.BaseMapper;
import club.goldri.web.system.domain.SysDatalog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysDatalogMapper extends BaseMapper<SysDatalog> {

    //获取最大版本号
    @Select("select max(sd.version_number) from sys_datalog sd where sd.table_name= #{tableName} and sd.data_id= #{dataId}")
    Integer getMaxVersionNum(@Param("tableName") String tableName, @Param("dataId") String dataId);

}