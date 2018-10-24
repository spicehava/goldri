package club.goldri.web.system.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "sys_dict")
public class SysDict implements Serializable {

    /**
     * 字典类型
     */
    @Id
    private String type;

    /**
     * 值
     */
    @Id
    private String code;

    /**
     * 显示信息
     */
    private String label;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否设计字段：用于判断该字段是否存在系统硬编码或增加该类型值对系统无效果，对于标识为是的字段不可以修改、删除。数据字典：YesOrNo
     */
    @Column(name = "is_design")
    private String isDesign;

    /**
     * 扩展字段，可用于联动
     */
    private String extValue;

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
     * 获取字典类型
     *
     * @return type - 字典类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置字典类型
     *
     * @param type 字典类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取值
     *
     * @return code - 值
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置值
     *
     * @param code 值
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取显示信息
     *
     * @return label - 显示信息
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置显示信息
     *
     * @param label 显示信息
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtValue() {
        return extValue;
    }

    public void setExtValue(String extValue) {
        this.extValue = extValue;
    }

    /**
     * 获取是否设计字段：用于判断该字段是否存在系统硬编码或增加该类型值对系统无效果，对于标识为是的字段不可以修改、删除。数据字典：YesOrNo
     *
     * @return is_design - 是否设计字段：用于判断该字段是否存在系统硬编码或增加该类型值对系统无效果，对于标识为是的字段不可以修改、删除。数据字典：YesOrNo
     */
    public String getIsDesign() {
        return isDesign;
    }

    /**
     * 设置是否设计字段：用于判断该字段是否存在系统硬编码或增加该类型值对系统无效果，对于标识为是的字段不可以修改、删除。数据字典：YesOrNo
     *
     * @param isDesign 是否设计字段：用于判断该字段是否存在系统硬编码或增加该类型值对系统无效果，对于标识为是的字段不可以修改、删除。数据字典：YesOrNo
     */
    public void setIsDesign(String isDesign) {
        this.isDesign = isDesign;
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

    @Override
    public String toString() {
        return "SysDict{" +
                "type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", label='" + label + '\'' +
                ", sort=" + sort +
                ", description='" + description + '\'' +
                ", isDesign='" + isDesign + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", updateBy='" + updateBy + '\'' +
                ", updateDate=" + updateDate +
                ", delFlag='" + delFlag + '\'' +
                ", page=" + page +
                ", rows=" + rows +
                '}';
    }
}