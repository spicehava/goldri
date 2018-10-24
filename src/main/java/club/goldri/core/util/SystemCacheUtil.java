package club.goldri.core.util;

import club.goldri.web.system.domain.SysUser;

import javax.servlet.http.HttpServletRequest;


/**
 * 使用规则：
 * 获取缓存后，通过"前缀（可选）+ 唯一标识"作为该缓存的key
 * 前缀用于区分是哪个表的业务数据，标识可以是id可以是username等
 */
public class SystemCacheUtil {

    /*
     *   |- 系统缓存
     *      |- 登录错误次数     key:error_count + username
     *      |- OTHER
     *   |- 用户缓存（userCache）
     *       |- 按id存储         key: userId- + id            value:SysUser
     *       |- 按username存储   key: username- + username    value:SysUser
     *   |- 资源缓存（resourceCache）
     *       |- 系统资源         key:id                        value:SysResource
     *       |- 用户资源         key:userid+id                 value:List<SysResource>
     *       |- 角色资源         key:roleId + id               value:List<SysResource>
     *   |- 角色缓存（roleCache）
     *       |- 系统角色         key:id                        value:SysRole
     *   |- 组织缓存（orgCache）
     *       |- 系统组织         key:id                        value:SysOrg
     */
    public static final String CACHE_USER = "userCache";
    public static final String CACHE_RESOURCE = "resourceCache";
    public static final String CACHE_ROLE = "roleCache";
    public static final String CACHE_ORG = "orgCache";
    public static final String CACHE_DICT = "dictCache";

    /**
     * key前缀
     * 角色、组织、用户角色不使用缓存
     */
    //用户-id
    public static final String SYS_USER_ID = "userId-";
    //用户-username
    public static final String SYS_USER_USERNAME = "username-";
    //用户菜单
    public static final String SYS_USER_MENU = "userMenu-";
    //用户权限
    public static final String SYS_USER_PERMISSION = "userPermission-";
    //角色-ID
    public static final String SYS_ROLE_ID = "roleId-";
    //部门
    public static final String SYS_ORG_ID = "orgId-";
    //字典type+code
    public static final String SYS_DICT_TC = "dictTc-";
    //字典type
    public static final String SYS_DICT_TYPE = "dictType-";
    //资源菜单
    public static final String SYS_MENU_MAP = "menuNamePathMap";
    /**
     * 获取用户对象
     * @param request
     * @return
     */
    public static SysUser getUserByRequest(HttpServletRequest request){
        String username = request.getHeader("username");
        return (SysUser)CacheUtil.get(CACHE_USER, SystemCacheUtil.SYS_USER_USERNAME + username);
    }

    /**
     * 根据用户id获取缓存中的用户数据
     * @param userId
     * @return
     */
    public static SysUser getUserById(String userId){
        return (SysUser)CacheUtil.get(CACHE_USER, SystemCacheUtil.SYS_USER_ID + userId);
    }

    /**
     * 根据username获取缓存中的用户数据
     * @param username
     * @return
     */
    public static SysUser getUserByUsername(String username){
        return (SysUser)CacheUtil.get(CACHE_USER, SystemCacheUtil.SYS_USER_USERNAME + username);
    }
}
