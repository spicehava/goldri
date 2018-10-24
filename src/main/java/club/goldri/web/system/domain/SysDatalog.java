package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import javax.persistence.*;

@Table(name = "sys_datalog")
public class SysDatalog extends BaseDomain {
    /**
     * CREATE TABLE 
     */
    @Column(name = "table_name")
    private String tableName;

    /**
     * 数据ID
     */
    @Column(name = "data_id")
    private String dataId;

    /**
     * 版本号：新增版本号为1，以后每次修改递增1
     */
    @Column(name = "version_number")
    private Integer versionNumber;

    /**
     * 创建人姓名
     */
    private String creater;

    /**
     * 数据内容：json格式
     */
    @Column(name = "data_content")
    private String dataContent;

    /**
     * 获取CREATE TABLE 
     *
     * @return table_name - CREATE TABLE 
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置CREATE TABLE 
     *
     * @param tableName CREATE TABLE 
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取数据ID
     *
     * @return data_id - 数据ID
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 设置数据ID
     *
     * @param dataId 数据ID
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取版本号：新增版本号为1，以后每次修改递增1
     *
     * @return version_number - 版本号：新增版本号为1，以后每次修改递增1
     */
    public Integer getVersionNumber() {
        return versionNumber;
    }

    /**
     * 设置版本号：新增版本号为1，以后每次修改递增1
     *
     * @param versionNumber 版本号：新增版本号为1，以后每次修改递增1
     */
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * 获取创建人姓名
     *
     * @return creater - 创建人姓名
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人姓名
     *
     * @param creater 创建人姓名
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * 获取数据内容：json格式
     *
     * @return data_content - 数据内容：json格式
     */
    public String getDataContent() {
        return dataContent;
    }

    /**
     * 设置数据内容：json格式
     *
     * @param dataContent 数据内容：json格式
     */
    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }
}