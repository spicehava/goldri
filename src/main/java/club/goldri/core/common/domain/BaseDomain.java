package club.goldri.core.common.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class BaseDomain implements Serializable{

    /**
     * 主键：所有表的主键都是id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//通过在TkMapperConfig生成策略
    private String id;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 删除标识：默认NORMAL。数据字典：DelFlag
     */
    private String delFlag = "NORMAL";

    @Transient
    private Integer page = 1;

    @Transient
    private Integer rows = 10;

    /**
     * 获取主键：所有表的主键都是id
     *
     * @return id - 主键：所有表的主键都是id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键：所有表的主键都是id
     *
     * @param id 主键：所有表的主键都是id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取创建人
     *
     * @return create_by - 创建人
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置创建人
     *
     * @param createBy 创建人
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 获取创建时间
     *
     * @return create_date - 创建时间
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置创建时间
     *
     * @param createDate 创建时间
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取更新人
     *
     * @return update_by - 更新人
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 设置更新人
     *
     * @param updateBy 更新人
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 获取更新时间
     *
     * @return update_date - 更新时间
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 设置更新时间
     *
     * @param updateDate 更新时间
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 获取删除标识：默认NORMAL。数据字典：DelFlag
     *
     * @return del_flag - 删除标识：默认NORMAL。数据字典：DelFlag
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 设置删除标识：默认NORMAL。数据字典：DelFlag
     *
     * @param delFlag 删除标识：默认NORMAL。数据字典：DelFlag
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
