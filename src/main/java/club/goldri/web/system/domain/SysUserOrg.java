package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import javax.persistence.*;

@Table(name = "sys_user_org")
public class SysUserOrg extends BaseDomain {
    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private String orgId;

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}