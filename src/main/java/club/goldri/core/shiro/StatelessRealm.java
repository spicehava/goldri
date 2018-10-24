package club.goldri.core.shiro;

import club.goldri.core.common.exception.ForbiddenException;
import club.goldri.core.util.StringUtil;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.service.ResourceService;
import club.goldri.core.constant.AuthConstant;
import club.goldri.core.util.CacheUtil;
import club.goldri.core.util.JwtTokenUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysResource;
import club.goldri.web.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

public class StatelessRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ResourceService resourceService;

    @Override
    public boolean supports(AuthenticationToken token) {
        /**
         * 仅支持StatelessToken 类型的Token，
         * 那么如果在StatelessAuthcFilter类中返回的是UsernamePasswordToken，那么将会报如下错误信息：
         * Please ensure that the appropriate Realm implementation is configured correctly or
         * that the realm accepts AuthenticationTokens of this type.StatelessAuthcFilter.isAccessAllowed()
         */
        return token instanceof StatelessToken;
    }

    /**
     * 认证方法
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        StatelessToken statelessToken = (StatelessToken) token;
        String username = statelessToken.getUsername();

        Claims claims = JwtTokenUtil.parseToken(((StatelessToken) token).getAuthToken(), AuthConstant.SECRETKET);

        if(username.equals(claims.getSubject())){
            //验证用户是否在缓存中，如果不存在则添加到缓存
            Object obj = CacheUtil.get(SystemCacheUtil.CACHE_USER, username);
            if(obj == null){
                SysUser sysUser = this.userService.selectUserByUsername(username);
                if(sysUser == null){
                    throw new ForbiddenException("用户不存在或已被删除！");
                }
            }
            //直接通过认证(
            return new SimpleAuthenticationInfo(
                    username,
                    ((StatelessToken) token).getAuthToken(),
                    getName());
        } else {
            return null;//throw new AuthenticationException("授权认证失败！");
        }

    }

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //根据用户名查找角色，请根据需求实现
        String username = (String) principals.getPrimaryPrincipal();
        SysUser sysUser = (SysUser) CacheUtil.get(SystemCacheUtil.CACHE_USER, SystemCacheUtil.SYS_USER_USERNAME + username);

        List<SysResource> resourcesList = this.resourceService.listResourceByUserId(sysUser.getId());
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if(resourcesList != null){
            for(SysResource resource: resourcesList){
                if (StringUtil.isNotBlank(resource.getPermission())){
                    //添加基于Permission的权限信息
                    for (String permission : StringUtil.split(resource.getPermission(),",")){
                        info.addStringPermission(permission);
                    }
                }
            }
        }

        return info;
    }
}
