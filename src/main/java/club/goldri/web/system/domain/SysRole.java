package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import javax.persistence.*;
import java.util.List;

@Table(name = "sys_role")
public class SysRole extends BaseDomain {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    //-----------------------------------------------------扩展字段-----------------------------------------------------
    /**
     * 角色资源
     */
    @Transient
    private List<SysResource> resourceList;

    @Transient
    private List<String> resourceIdList;

    @Transient
    private List<String> userIdList;

    /**
     * 获取角色名称
     *
     * @return name - 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取角色描述
     *
     * @return description - 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置角色描述
     *
     * @param description 角色描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public List<SysResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<SysResource> resourceList) {
        this.resourceList = resourceList;
    }

    public List<String> getResourceIdList() {
        return resourceIdList;
    }

    public void setResourceIdList(List<String> resourceIdList) {
        this.resourceIdList = resourceIdList;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }
}