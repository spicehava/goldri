package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;

import javax.persistence.*;
import java.util.List;

@Table(name = "sys_org")
public class SysOrg extends BaseDomain {
    /**
     * 组织名称
     */
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 父组织ID
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 子记录
     */
    @Transient
    private List<SysOrg> childs;
    /**
     * 获取组织名称
     *
     * @return name - 组织名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置组织名称
     *
     * @param name 组织名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取组织描述
     *
     * @return description - 组织描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置组织描述
     *
     * @param description 组织描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取父组织ID
     *
     * @return parent_id - 父组织ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置父组织ID
     *
     * @param parentId 父组织ID
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<SysOrg> getChilds() {
        return childs;
    }

    public void setChilds(List<SysOrg> childs) {
        this.childs = childs;
    }
}