package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import javax.persistence.*;

@Table(name = "sys_role_resource")
public class SysRoleResource extends BaseDomain {
    /**
     * 角色ID
     */
    @Column(name = "role_id")
    private String roleId;

    /**
     * 资源ID
     */
    @Column(name = "resource_id")
    private String resourceId;

    /**
     * 获取角色ID
     *
     * @return role_id - 角色ID
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * 设置角色ID
     *
     * @param roleId 角色ID
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取资源ID
     *
     * @return resource_id - 资源ID
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * 设置资源ID
     *
     * @param resourceId 资源ID
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}