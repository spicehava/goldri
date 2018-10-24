package club.goldri.web.system.service.impl;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.BeanUtil;
import club.goldri.web.system.domain.*;
import club.goldri.web.system.mapper.*;
import com.github.pagehelper.PageHelper;
import club.goldri.core.common.service.AbstractService;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.*;
import club.goldri.web.system.mapper.*;
import club.goldri.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class UserServiceImpl extends AbstractService<SysUser> implements UserService{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserOrgMapper sysUserOrgMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysOrgMapper sysOrgMapper;

    /**
     * 查询方法，支持模糊查询
     * @param sysUser
     * @return
     */
    @Override
    public List<SysUser> listAll(SysUser sysUser){
        if (sysUser.getPage() != null && sysUser.getRows() != null) {
            PageHelper.startPage(sysUser.getPage(), sysUser.getRows());
        }
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        //search condition
        //用户名
        if(sysUser != null && sysUser.getUsername() != null && !"".equals(sysUser.getUsername())){
            criteria.andLike("username","%" + sysUser.getUsername() + "%");
        }
        //姓名
        if(sysUser != null && sysUser.getName() != null && !"".equals(sysUser.getName())){
            criteria.andLike("name","%" + sysUser.getName() + "%");
        }
        //职位
        if(sysUser != null && sysUser.getPosition() != null && !"".equals(sysUser.getPosition())){
            criteria.andLike("position","%" + sysUser.getPosition() + "%");
        }
        //状态
        if(sysUser != null && sysUser.getState() != null && !"".equals(sysUser.getState())){
            criteria.andEqualTo("state", sysUser.getState());
        }
        if(sysUser != null && sysUser.getId() != null && !"".equals(sysUser.getId())){
            criteria.andEqualTo("id", sysUser.getId());
        }

        //获取删除标记为正常的记录
        criteria.andEqualTo("delFlag", sysUser.getDelFlag());

        return this.sysUserMapper.selectByExample(example);
    }

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * 当数据发生变化时更新缓存
     * 注意事项：username一定要传
     * @param sysUser
     * @return
     */
    @Caching(put = {
            @CachePut(value=SystemCacheUtil.CACHE_USER , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_USERNAME + #sysUser.username", condition = "#sysUser.username != null"),
            @CachePut(value=SystemCacheUtil.CACHE_USER , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #sysUser.id", condition = "#sysUser.id != null"),
            @CachePut(value=SystemCacheUtil.CACHE_USER
                    , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_USERNAME + T(club.goldri.core.util.SystemCacheUtil).getUserById(#sysUser.id)?.getUsername()"
                    , condition = "#sysUser.id != null and #sysUser.username == null"),
            @CachePut(value=SystemCacheUtil.CACHE_USER
                    , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + T(club.goldri.core.util.SystemCacheUtil).getUserByUsername(#sysUser.username)?.getId()"
                    , condition = "#sysUser.username != null and #sysUser.id == null")
    })
    @Transactional
    @Override
    public SysUser commonSave(SysUser currentUser, SysUser sysUser){
        if(sysUser.getId() == null){
            super.save(sysUser);
        } else {
            this.updateNotNull(sysUser);
        }

        //判断用户的组织是否为空，不为空时需要对部门处理
        if(sysUser != null && sysUser.getOrgList() != null && sysUser.getOrgList().size() > 0 ){
            List<SysOrg> orgList = sysUser.getOrgList();
            Set<String> orgIdList = new HashSet<String>();
            //获取前台传过来的orgid
            for(SysOrg sysOrg : orgList){
                orgIdList.add(sysOrg.getId());
            }

            //获取当前用户所属部门
            Example example = new Example(SysUserOrg.class);
            Example.Criteria criteria = example.createCriteria();
            //获取删除标记为正常的记录
            criteria.andEqualTo("userId", sysUser.getId());
            criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

            //用户的当前组织列表
            List<SysUserOrg> currUserOrgList = this.sysUserOrgMapper.selectByExample(example);

            Set<String> currUserOrgIdList = new HashSet<String>();
            if(currUserOrgList != null && currUserOrgList.size()> 0){
                for(SysUserOrg sysUserOrg : currUserOrgList){
                    currUserOrgIdList.add(sysUserOrg.getOrgId());
                }
            }

            //比对用户的部门变化（新增、删除）
            Map<String,Set<String>> map = BeanUtil.diffList(orgIdList, currUserOrgIdList);

            Set<String> addUserOrgIdList = map.get(BeanUtil.ADD);
            Set<String> removeSysUserOrgList = map.get(BeanUtil.REMOVE);

            //删除数据
            if(removeSysUserOrgList != null && removeSysUserOrgList.size() > 0){
                for(String removeOrgId : removeSysUserOrgList){
                    SysUserOrg sysUserOrg = new SysUserOrg();
                    BeanUtil.setUpdateUser(currentUser, sysUserOrg);
                    sysUserOrg.setDelFlag(Constant.DEL_FLAG_DELETE);

                    Example remExample = new Example(SysUserOrg.class);
                    Example.Criteria remCriteria = remExample.createCriteria();
                    //获取删除标记为正常的记录
                    remCriteria.andEqualTo("userId", sysUser.getId());
                    remCriteria.andEqualTo("orgId", removeOrgId);
                    remCriteria.andNotEqualTo("delFlag", Constant.DEL_FLAG_DELETE);
                    this.sysUserOrgMapper.updateByExampleSelective(sysUserOrg, remExample);
                }
            }

            //添加数据
            if(addUserOrgIdList != null && addUserOrgIdList.size() > 0){
                for(String orgId : addUserOrgIdList){
                    SysUserOrg sysUserOrg = new SysUserOrg();
                    sysUserOrg.setUserId(sysUser.getId());
                    sysUserOrg.setOrgId(orgId);

                    BeanUtil.setCreateUser(currentUser, sysUserOrg);
                    BeanUtil.setUpdateUser(currentUser, sysUserOrg);

                    this.sysUserOrgMapper.insert(sysUserOrg);
                }
            }
        }

        //处理用户角色
        if(sysUser != null && sysUser.getRoleList() != null && sysUser.getRoleList().size() > 0 ){
            List<SysRole> roleList = sysUser.getRoleList();
            Set<String> roleIdList = new HashSet<String>();
            //获取前台传过来的roleId
            for(SysRole sysRole : roleList){
                roleIdList.add(sysRole.getId());
            }

            //获取当前用户所有角色
            Example example = new Example(SysUserRole.class);
            Example.Criteria criteria = example.createCriteria();
            //获取删除标记为正常的记录
            criteria.andEqualTo("userId", sysUser.getId());
            criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

            //用户的当前角色列表
            List<SysUserRole> currUserRoleList = this.sysUserRoleMapper.selectByExample(example);

            Set<String> currUserRoleIdList = new HashSet<String>();
            if(currUserRoleList != null && currUserRoleList.size()> 0){
                for(SysUserRole sysUserRole : currUserRoleList){
                    currUserRoleIdList.add(sysUserRole.getRoleId());
                }
            }

            //比对用户的角色变化（新增、删除）
            Map<String,Set<String>> map = BeanUtil.diffList(roleIdList, currUserRoleIdList);

            Set<String> addUserRoleIdList = map.get(BeanUtil.ADD);
            Set<String> removeSysUserRoleList = map.get(BeanUtil.REMOVE);

            //删除数据(软删除）
            if(removeSysUserRoleList != null && removeSysUserRoleList.size() > 0){
                for(String removeRoleId : removeSysUserRoleList){
                    SysUserRole sysUserRole = new SysUserRole();
                    BeanUtil.setUpdateUser(currentUser, sysUserRole);
                    sysUserRole.setDelFlag(Constant.DEL_FLAG_DELETE);

                    Example remExample = new Example(SysUserRole.class);
                    Example.Criteria remCriteria = remExample.createCriteria();
                    //获取删除标记为正常的记录
                    remCriteria.andEqualTo("userId", sysUser.getId());
                    remCriteria.andEqualTo("roleId", removeRoleId);
                    remCriteria.andNotEqualTo("delFlag", Constant.DEL_FLAG_DELETE);
                    this.sysUserRoleMapper.updateByExampleSelective(sysUserRole, remExample);
                }
            }

            //添加数据
            if(addUserRoleIdList != null && addUserRoleIdList.size() > 0){
                for(String roleId : addUserRoleIdList){
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRole.setRoleId(roleId);

                    BeanUtil.setCreateUser(currentUser, sysUserRole);
                    BeanUtil.setUpdateUser(currentUser, sysUserRole);

                    this.sysUserRoleMapper.insert(sysUserRole);
                }
            }
        }

        sysUser = this.selectUserById(sysUser.getId());
        return sysUser;
    }

    @Cacheable(value = SystemCacheUtil.CACHE_USER, key = "T(club.goldri.core.util.SystemCacheUtil).SYS_USER_USERNAME + #username")
    @Override
    public SysUser selectUserByUsername(String username) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        List<SysUser> userList = this.listByExample(example);
        if(userList.size()>0){
            return this.selectUserById(userList.get(0).getId());
        }
        return null;
    }

    /**
     * 根据用户ID获取用户信息（封装了部门、角色）
     *
     * @param userId
     * @return
     */
    @Cacheable(value = SystemCacheUtil.CACHE_USER, key = "T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #userId")
    @Override
    public SysUser selectUserById(String userId) {
        SysUser sysUser = null;
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        List<SysUser> userList = this.listByExample(example);
        if(userList.size() == 1){
            sysUser = userList.get(0);
        }
        if(sysUser != null){
            List<SysRole> roleList = this.sysRoleMapper.listRoleByUserId(userId);

            List<SysOrg> orgList = this.sysOrgMapper.listOrgByUserId(userId);

            sysUser.setOrgList(orgList);
            sysUser.setRoleList(roleList);
        }
        return sysUser;
    }

    /**
     * 软删除用户
     * @param userId
     * @return
     * 注意：清除用户缓存一定要先清除username，因为username需要借助userid
     */
    @Caching(
            evict = {
                    @CacheEvict(value=SystemCacheUtil.CACHE_USER
                            , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_USERNAME + T(club.goldri.core.util.SystemCacheUtil).getUserById(#userId)?.getUsername()"),
                    @CacheEvict(value=SystemCacheUtil.CACHE_USER , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #userId"),
                    @CacheEvict(value=SystemCacheUtil.CACHE_RESOURCE , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #userId"),
                    @CacheEvict(value=SystemCacheUtil.CACHE_ROLE , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #userId"),
                    @CacheEvict(value=SystemCacheUtil.CACHE_ORG , key="T(club.goldri.core.util.SystemCacheUtil).SYS_USER_ID + #userId")
            })
    @Override
    public SysUser removeUserById(SysUser currentUser , String userId){
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setDelFlag(Constant.DEL_FLAG_DELETE);
        BeanUtil.setUpdateUser(currentUser, sysUser);
        sysUser = this.commonSave(currentUser, sysUser);

        //删除用户组织关系（软删除）
        SysUserOrg sysUserOrg = new SysUserOrg();
        sysUserOrg.setDelFlag(Constant.DEL_FLAG_DELETE);

        Example example = new Example(SysUserOrg.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        this.sysUserOrgMapper.updateByExampleSelective(sysUserOrg,example);

        //删除用户角色关系（软删除）
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setDelFlag(Constant.DEL_FLAG_DELETE);

        Example example2 = new Example(SysUserRole.class);
        Example.Criteria criteria2 = example2.createCriteria();

        criteria2.andEqualTo("userId",userId);
        criteria2.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        this.sysUserRoleMapper.updateByExampleSelective(sysUserRole,example2);
        return sysUser;
    }

    /**
     * 根据roleId获取用户
     *
     * @param roleId
     * @return
     */
    @Override
    public List<SysUser> listUserByRoleId(String roleId) {

        return this.sysUserMapper.listUserByRoleId(roleId);
    }
}
