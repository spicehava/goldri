package club.goldri.web.system.service.impl;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.BeanUtil;
import club.goldri.core.util.ReflectUtil;
import club.goldri.core.util.StringUtil;
import club.goldri.web.system.domain.DatalogDiff;
import club.goldri.web.system.domain.SysDatalog;
import com.github.pagehelper.PageHelper;
import club.goldri.core.common.service.AbstractService;
import club.goldri.core.util.JSONHelper;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.mapper.SysDatalogMapper;
import club.goldri.web.system.service.DatalogService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.*;

@Service
public class DatalogServiceImpl extends AbstractService<SysDatalog> implements DatalogService {

    @Autowired
    private SysDatalogMapper sysDatalogMapper;

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysDatalog
     * @return
     */
    @Override
    public List<SysDatalog> listAll(SysDatalog sysDatalog) {
        if (sysDatalog.getPage() != null && sysDatalog.getRows() != null) {
            PageHelper.startPage(sysDatalog.getPage(), sysDatalog.getRows());
        }

        Example example = new Example(SysDatalog.class);
        Example.Criteria criteria = example.createCriteria();

        if (sysDatalog != null && sysDatalog.getId() != null) {
            criteria.andEqualTo("id", sysDatalog.getId());
        }

        if (sysDatalog != null && sysDatalog.getTableName() != null) {
            criteria.andEqualTo("tableName", sysDatalog.getTableName());
        }

        if (sysDatalog != null && sysDatalog.getDataContent() != null) {
            criteria.andLike("dataContent", "%" + sysDatalog.getDataContent() + "%");
        }

        //TODO other search params

        //获取删除标记为正常的记录
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        return this.sysDatalogMapper.selectByExample(example);
    }

    /**
     * 新增数据日志
     *
     * @param currentUser
     * @param object
     * @param dataId 可以为空，当为空时object的id属性值不可为空
     * @throws IllegalAccessException，获取不到dataId时抛出该异常
     */
    @Override
    public void saveDataLog(SysUser currentUser, Object object, String dataId) throws IllegalAccessException {
        logger.info("保存数据日志:" + object.getClass());
        String tableName = "";

        Annotation annotation =  object.getClass().getAnnotation(Table.class);
        if(annotation != null){
            tableName = ((Table)annotation).name();
        } else {
            tableName = object.getClass().getSimpleName();
        }
        if(StringUtil.isEmpty(dataId)){
            Object objField = ReflectUtil.getFieldValue(object, "id");

            if(objField != null){
                dataId = objField.toString();
            }
        }

        //处理异常
        if(dataId == null || "".equals(dataId)){
            throw new IllegalAccessException("dataId为空，不可保存");
        }

        this.saveDatalog(currentUser, tableName, dataId, JSONHelper.bean2json(object));
    }

    /**
     * 保存业务日志
     * @param currentUser
     * @param tableName
     * @param dataId
     * @param dataContent
     */
    @Override
    public void saveDatalog(SysUser currentUser, String tableName, String dataId, String dataContent) {

        //获取版本号
        int versionNum = 0;

        Integer integer = this.sysDatalogMapper.getMaxVersionNum(tableName, dataId);

        if (integer != null) {
            versionNum = integer.intValue();
        }

        //持久化数据
        SysDatalog sysDatalog = new SysDatalog();
        sysDatalog.setTableName(tableName);
        sysDatalog.setDataId(dataId);
        sysDatalog.setDataContent(dataContent);
        sysDatalog.setVersionNumber(versionNum + 1);

        BeanUtil.setCreateUser(currentUser, sysDatalog);
        BeanUtil.setUpdateUser(currentUser, sysDatalog);

        //如果本次数据与最后一条数据信息一致，则不记录
        List<SysDatalog> dataLogList = this.getLastDataLog(tableName, dataId);
        if(dataLogList != null && dataLogList.size() > 0){
            SysDatalog dataLog = dataLogList.get(0);
            if(dataLog.getDataContent() != null && dataLog.getDataContent().equals(sysDatalog.getDataContent())){
                logger.info("本次未发生信息修改，不做日志记录：tableName=" + sysDatalog.getTableName());
                return ;
            }
        }
        logger.info("开始持久化数据日志:tableName=" + tableName + ", dataId=" + dataId);
        this.save(sysDatalog);
    }

    /**
     * 获取所有版本
     *
     * @param tableName
     * @param dataId
     * @return
     */
    @Override
    public List<SysDatalog> listVersionNumber(String tableName, String dataId) {
        Example example = new Example(SysDatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tableName", tableName);
        criteria.andEqualTo("dataId", dataId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysDatalog> dataLogList = this.sysDatalogMapper.selectByExample(example);
        return dataLogList;
    }

    /**
     * 根据指定的两个版本做数据比对
     *
     * @param tableName
     * @param dataId
     * @param version1
     * @param version2
     * @return
     */
    @Override
    public List<DatalogDiff> diffDataVersion(String tableName, String dataId, String version1, String version2) {

        SysDatalog dataLog1 = this.getDataLog(tableName, dataId, version1);
        SysDatalog dataLog2 = this.getDataLog(tableName, dataId, version2);

        return this.compareData(dataLog1, dataLog2);
    }

    /**
     * 根据版本的最后一次和上一次的数据，进行比较返回
     * @param tableName
     * @param dataId
     * @return
     */
    @Override
    public List<DatalogDiff> diffLastVersion(String tableName, String dataId){

        List<SysDatalog> dataLogList = this.getLastDataLog(tableName, dataId);

        SysDatalog dataLog1 = null;
        SysDatalog dataLog2 = null;

        if(dataLogList.size() == 0){
            return null;
        } else if(dataLogList.size() == 1){
            dataLog1 = dataLog2 = dataLogList.get(0);
        } else {
            dataLog1 = dataLogList.get(0);
            dataLog2 = dataLogList.get(1);
        }

        return this.compareData(dataLog1, dataLog2);
    }
//--------------------------------------------------- 工具方法 ---------------------------------------------------------

    /**
     * 根据版本号获取业务日志
     * @param tableName
     * @param dataId
     * @param versionNumber
     * @return
     */
    public SysDatalog getDataLog(String tableName, String dataId, String versionNumber) {

        Example example = new Example(SysDatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tableName", tableName);
        criteria.andEqualTo("dataId", dataId);
        criteria.andEqualTo("versionNumber", versionNumber);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysDatalog> dataLogList = this.sysDatalogMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(dataLogList)){
            return dataLogList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 返回最后两条修改的数据
     * @param tableName
     * @param dataId
     * @return
     */
    public List<SysDatalog> getLastDataLog(String tableName, String dataId) {

        Example example = new Example(SysDatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tableName", tableName);
        criteria.andEqualTo("dataId", dataId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        //设置排序
        example.setOrderByClause("createDate desc");
        //仅获取最后两条记录
        PageHelper.startPage(1, 2);
        List<SysDatalog> dataLogList = this.sysDatalogMapper.selectByExample(example);
        return dataLogList;
    }

    /**
     * 比较两个版本的数据
     * @param dataLog1
     * @param dataLog2
     * @return List<DatalogDiff>
     */
    public List<DatalogDiff> compareData(SysDatalog dataLog1, SysDatalog dataLog2){
        if (dataLog1 != null && dataLog2 != null) {
            //正则用于去掉头尾的[]字符(如存在)
            Integer version1 = dataLog1.getVersionNumber();
            Integer version2 = dataLog2.getVersionNumber();
            Map<String, Object> map1 = null;
            Map<String, Object> map2 = null;

            if (version1 < version2) {
                map1 = JSONHelper.toLinkedHashMap(dataLog1.getDataContent().replaceAll("^\\[|\\]$", ""));
                map2 = JSONHelper.toLinkedHashMap(dataLog2.getDataContent().replaceAll("^\\[|\\]$", ""));
            }else{
                map1 = JSONHelper.toLinkedHashMap(dataLog2.getDataContent().replaceAll("^\\[|\\]$", ""));
                map2 = JSONHelper.toLinkedHashMap(dataLog1.getDataContent().replaceAll("^\\[|\\]$", ""));
            }

            Map<String, Object> mapAll = new LinkedHashMap<>();
            mapAll.putAll(map1);
            mapAll.putAll(map2);
            Set<String> set = mapAll.keySet();

            List<DatalogDiff> dataLogDiffs = new LinkedList<DatalogDiff>();

            String value1 = null;
            String value2 = null;
            for (String string : set) {
                DatalogDiff dataLogDiff = new DatalogDiff();
                dataLogDiff.setName(string);

                if (map1.containsKey(string)) {
                    value1 = map1.get(string).toString();
                    if (value1 == null) {
                        dataLogDiff.setValue1("");
                    }else {
                        dataLogDiff.setValue1(value1);
                    }
                }else{
                    dataLogDiff.setValue1("");
                }

                if (map2.containsKey(string)) {
                    value2 = map2.get(string).toString();
                    if (value2 == null) {
                        dataLogDiff.setValue2("");
                    }else {
                        dataLogDiff.setValue2(value2);
                    }
                }else {
                    dataLogDiff.setValue2("");
                }

                if (value1 == null && value2 == null) {
                    dataLogDiff.setDiff("N");//无修改
                }else {
                    if (value1 != null && value2 != null) {
                        if (value1.equals(value2)) {//相同
                            dataLogDiff.setDiff("N");
                        }else {
                            dataLogDiff.setDiff("Y");//有修改
                        }
                    }else {
                        dataLogDiff.setDiff("Y");
                    }
                }
                dataLogDiffs.add(dataLogDiff);
            }

            return dataLogDiffs;
        }

        return null;
    }
}
