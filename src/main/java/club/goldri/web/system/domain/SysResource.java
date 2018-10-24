package club.goldri.web.system.domain;

import club.goldri.core.common.domain.BaseDomain;
import club.goldri.web.system.domain.enums.ResourceType;

import javax.persistence.*;
import java.util.List;

@Table(name = "sys_resource")
public class SysResource extends BaseDomain {
    /**
     * 资源名称（菜单/按钮名称）
     */
    private String name;

    /**
     * URL地址
     */
    private String url;

    /**
     * 权限码
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 所在层级
     */
    private Integer level;

    /**
     * 资源类型。数据字典：ResourceType
     */
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     *  页面加载方式
     */
    private String target;
    /**
     * 父资源ID
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 创建人名称
     */
    private String creater;

    /**
     * 更新人名称
     */
    private String updater;

    /**
     * 子资源
     */
    @Transient
    private List<SysResource> childs;

    /**
     * 标识该资源是否选中
     */
    @Transient
    private Boolean checked;

    /**
     * 所有父id
     */
    @Transient
    private String parentIds;

    /**
     * 获取资源名称（菜单/按钮名称）
     *
     * @return name - 资源名称（菜单/按钮名称）
     */
    public String getName() {
        return name;
    }

    /**
     * 设置资源名称（菜单/按钮名称）
     *
     * @param name 资源名称（菜单/按钮名称）
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取URL地址
     *
     * @return url - URL地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置URL地址
     *
     * @param url URL地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取权限码
     *
     * @return permission - 权限码
     */
    public String getPermission() {
        return permission;
    }

    /**
     * 设置权限码
     *
     * @param permission 权限码
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * 获取图标
     *
     * @return icon - 图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标
     *
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取所在层级
     *
     * @return level - 所在层级
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置所在层级
     *
     * @param level 所在层级
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取资源类型。数据字典：ResourceType
     *
     * @return type - 资源类型。数据字典：ResourceType
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * 设置资源类型。数据字典：ResourceType
     *
     * @param type 资源类型。数据字典：ResourceType
     */
    public void setType(ResourceType type) {
        this.type = type;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 获取父资源ID
     *
     * @return parent_id - 父资源ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置父资源ID
     *
     * @param parentId 父资源ID
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取创建人名称
     *
     * @return creater - 创建人名称
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人名称
     *
     * @param creater 创建人名称
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * 获取更新人名称
     *
     * @return updater - 更新人名称
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * 设置更新人名称
     *
     * @param updater 更新人名称
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public List<SysResource> getChilds() {
        return childs;
    }

    public void setChilds(List<SysResource> childs) {
        this.childs = childs;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }
}