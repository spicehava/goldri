package club.goldri.web.system.service;


import club.goldri.core.common.service.BaseService;
import club.goldri.web.system.domain.DatalogDiff;
import club.goldri.web.system.domain.SysDatalog;
import club.goldri.web.system.domain.SysUser;

import java.util.List;

public interface DatalogService extends BaseService<SysDatalog> {

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysDatalog
     * @return
     */
    List<SysDatalog> listAll(SysDatalog sysDatalog);

    /**
     * 新增数据日志
     *
     * @param currentUser
     * @param object
     * @param dataId 可以为空，当为空时object的id属性值不可为空
     * @throws IllegalAccessException，获取不到dataId时抛出该异常
     */
    void saveDataLog(SysUser currentUser, Object object, String dataId) throws IllegalAccessException;

    /**
     * 新增数据日志
     * @param currentUser
     * @param tableName
     * @param dataId
     * @param dataContent
     */
    void saveDatalog(SysUser currentUser, String tableName, String dataId, String dataContent);

    /**
     * 获取所有版本
     * @param tableName
     * @param dataId
     * @return
     */
    List<SysDatalog> listVersionNumber(String tableName, String dataId);

    /**
     * 根据指定的两个版本做数据比对
     * @param tableName
     * @param dataId
     * @param version1
     * @param version2
     * @return
     */
    List<DatalogDiff> diffDataVersion(String tableName, String dataId, String version1, String version2);

    /**
     * 根据版本的最后一次和上一次的数据，进行比较返回
     * @param tableName
     * @param dataId
     * @return
     */
    List<DatalogDiff> diffLastVersion(String tableName, String dataId);
}
