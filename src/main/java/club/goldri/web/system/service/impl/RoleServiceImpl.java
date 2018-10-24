package club.goldri.web.system.service.impl;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.BeanUtil;
import club.goldri.web.system.domain.*;
import club.goldri.web.system.mapper.SysRoleResourceMapper;
import club.goldri.web.system.service.ResourceService;
import club.goldri.web.system.service.RoleService;
import com.github.pagehelper.PageHelper;
import club.goldri.core.common.service.AbstractService;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.*;
import club.goldri.web.system.mapper.SysResourceMapper;
import club.goldri.web.system.mapper.SysRoleMapper;
import club.goldri.web.system.mapper.SysUserRoleMapper;
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
public class RoleServiceImpl extends AbstractService<SysRole> implements RoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;

    @Autowired
    private SysResourceMapper sysResourceMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private ResourceService resourceService;

    /**
     * 查询，支持模糊查询
     * @param sysResource
     * @return
     */
    @Override
    public List<SysRole> listAll(SysRole sysResource){
        if (sysResource.getPage() != null && sysResource.getRows() != null) {
            PageHelper.startPage(sysResource.getPage(), sysResource.getRows());
        }
        Example example = new Example(SysRole.class);
        Example.Criteria criteria = example.createCriteria();
        if(sysResource != null && sysResource.getName() != null){
            criteria.andLike("name","%" + sysResource.getName() + "%");
        }
        criteria.andEqualTo("id", sysResource.getId());
        //TODO other search params

        //获取删除标记为正常的记录
        criteria.andEqualTo("delFlag", sysResource.getDelFlag());

        return this.sysRoleMapper.selectByExample(example);
    }

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * 当数据发生变化时更新缓存
     * @param sysRole
     * @return
     */
    @CacheEvict(value = SystemCacheUtil.CACHE_RESOURCE
            , key = "T(club.goldri.core.util.SystemCacheUtil).SYS_ROLE_ID + #sysRole.id"
            , condition = "#sysRole.resourceList != null and #sysRole.resourceList.size() > 0")
    @CachePut(value= SystemCacheUtil.CACHE_ROLE, key = "#sysRole.id")
    @Override
    @Transactional
    public SysRole commonSave(SysUser currentUser, SysRole sysRole){
        if(sysRole.getId() == null){
            super.save(sysRole);
        } else {
            this.updateNotNull(sysRole);
        }

        //判断角色资源
        if(sysRole.getResourceList() != null && sysRole.getResourceList().size() > 0){
            //前端传入的资源列表
            List<String> resIdList = new ArrayList<String>();
            for(SysResource sysRes : sysRole.getResourceList()){
                resIdList.add(sysRes.getId());
            }
            this.setResource(currentUser, resIdList, sysRole.getId());
        }

        return this.selectRoleById(sysRole.getId());
    }

    /**
     * 根据ID查询单个实体
     * @param id
     * @return
     */
    @Cacheable(value = SystemCacheUtil.CACHE_ROLE, key = "#id")
    @Override
    public SysRole selectRoleById(String id) {
        SysRole sysRole = null;

        Example example = new Example(SysRole.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",id);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysRole> roleList = this.listByExample(example);

        if(roleList.size() == 1){
            sysRole = roleList.get(0);
        }

        if(sysRole != null){
            List<SysResource> resourceListList = this.resourceService.listResourceByRoleId(id);
            sysRole.setResourceList(resourceListList);
        }

        return sysRole;
    }

    /**
     * 根据id删除单个实体(软删除）
     * @param currentUser
     * @param id
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value=SystemCacheUtil.CACHE_ROLE , key="#id"),
            @CacheEvict(value = SystemCacheUtil.CACHE_USER, allEntries = true),
            @CacheEvict(value=SystemCacheUtil.CACHE_RESOURCE, key="T(club.goldri.core.util.SystemCacheUtil).SYS_ROLE_ID + #id")
    })
    @Override
    @Transactional
    public SysRole removeRoleById(SysUser currentUser ,String id){
        SysRole sysRole = new SysRole();

        sysRole.setId(id);
        sysRole.setDelFlag(Constant.DEL_FLAG_DELETE);
        BeanUtil.setUpdateUser(currentUser, sysRole);
        SysRole role = this.commonSave(currentUser, sysRole);

        //需要删除角色用户关系表（软删除）
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setDelFlag(Constant.DEL_FLAG_DELETE);

        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("roleId",id);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        this.sysUserRoleMapper.updateByExampleSelective(sysUserRole,example);

        //删除角色资源关系
        SysRoleResource sysRoleResource = new SysRoleResource();
        sysRoleResource.setDelFlag(Constant.DEL_FLAG_DELETE);

        Example example2 = new Example(SysRoleResource.class);
        Example.Criteria criteria2 = example2.createCriteria();

        criteria2.andEqualTo("roleId",id);
        criteria2.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        this.sysRoleResourceMapper.updateByExampleSelective(sysRoleResource,example2);
        return role;
    }

    /**
     * 前端只传勾选的资源，后台将勾选的数据对应的父层级数据保存
     * @param resourceIdList
     * @param roleId
     */
    @Caching(evict = {
            @CacheEvict(value = SystemCacheUtil.CACHE_RESOURCE, key = "T(club.goldri.core.util.SystemCacheUtil).SYS_ROLE_ID + #roleId"),
            @CacheEvict(value = SystemCacheUtil.CACHE_USER, allEntries = true)
    })
    @Override
    public void setResource(SysUser currentUser, List<String> resourceIdList , String roleId){
        //1.根据资源id查询父层级ids
        List<String> parentList = this.sysResourceMapper.listResParentIdUp(resourceIdList);

        Set<String> allResIdList = new HashSet<String>();
        for(String str : parentList){
            if(str == null || "".equals(str)){
                continue;
            }
            String[]  arr = str.split(",");
            for(String s : arr){
                if("0".equals(s)){
                    continue;
                }
                allResIdList.add(s);
            }
        }

        //2.处理角色资源（新增或删除）
        Example example = new Example(SysRoleResource.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("roleId", roleId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysRoleResource> currRoleResList = this.sysRoleResourceMapper.selectByExample(example);

        Set<String> currResIdList = new HashSet<>();

        if(currRoleResList != null && currRoleResList.size() > 0){
            for(SysRoleResource roleResource : currRoleResList){
                currResIdList.add(roleResource.getResourceId());
            }
        }

        Map<String,Set<String>> map = BeanUtil.diffList(allResIdList, currResIdList);

        Set<String> addRoleResIdList = map.get(BeanUtil.ADD);
        Set<String> removeRoleResList = map.get(BeanUtil.REMOVE);

        //删除数据
        if(removeRoleResList != null && removeRoleResList.size() > 0){
            for(String removeResId : removeRoleResList){
                SysRoleResource reoleRes = new SysRoleResource();
                BeanUtil.setUpdateUser(currentUser, reoleRes);
                reoleRes.setDelFlag(Constant.DEL_FLAG_DELETE);

                Example remExample = new Example(SysRoleResource.class);
                Example.Criteria remCriteria = remExample.createCriteria();
                //获取删除标记为正常的记录
                remCriteria.andEqualTo("roleId", roleId);
                remCriteria.andEqualTo("resourceId", removeResId);
                remCriteria.andNotEqualTo("delFlag", Constant.DEL_FLAG_DELETE);
                this.sysRoleResourceMapper.updateByExampleSelective(reoleRes, remExample);
            }
        }

        //添加资源
        if(addRoleResIdList != null && addRoleResIdList.size() > 0){
            for(String resId : addRoleResIdList){
                SysRoleResource roleRes = new SysRoleResource();
                roleRes.setRoleId(roleId);
                roleRes.setResourceId(resId);

                BeanUtil.setCreateUser(currentUser, roleRes);
                BeanUtil.setUpdateUser(currentUser, roleRes);

                this.sysRoleResourceMapper.insert(roleRes);
            }
        }
    }

    /**
     * 为角色设置用户
     * @param currentUser
     * @param userIdList
     * @param roleId
     */
    @Override
    public void setUser(SysUser currentUser, List<String> userIdList, String roleId){
        Set<String> compareUserIdList = new HashSet<String>();
        if(userIdList != null && userIdList.size() > 0){
            for(String userId : userIdList){
                compareUserIdList.add(userId);
            }
        }

        //获取该角色的用户
        Example example = new Example(SysUserRole.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("roleId", roleId);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysUserRole> userRoleList = this.sysUserRoleMapper.selectByExample(example);

        Set<String> currUserIdList = new HashSet<String>();
        if(userRoleList != null && userRoleList.size() > 0){
            for(SysUserRole userRole : userRoleList){
                currUserIdList.add(userRole.getUserId());
            }
        }

        Map<String,Set<String>> map = BeanUtil.diffList(compareUserIdList, currUserIdList);

        Set<String> addUserIdList = map.get(BeanUtil.ADD);
        Set<String> removeUserIdList = map.get(BeanUtil.REMOVE);

        //删除数据
        if(removeUserIdList != null && removeUserIdList.size() > 0){
            for(String removeUserId : removeUserIdList){
                SysUserRole userRole = new SysUserRole();
                BeanUtil.setUpdateUser(currentUser, userRole);
                userRole.setDelFlag(Constant.DEL_FLAG_DELETE);

                Example remExample = new Example(SysUserRole.class);
                Example.Criteria remCriteria = remExample.createCriteria();
                //获取删除标记为正常的记录
                remCriteria.andEqualTo("userId", removeUserId);
                remCriteria.andEqualTo("roleId", roleId);
                remCriteria.andNotEqualTo("delFlag", Constant.DEL_FLAG_DELETE);
                this.sysUserRoleMapper.updateByExampleSelective(userRole, remExample);
            }
        }

        //添加数据
        if(addUserIdList != null && addUserIdList.size() > 0){
            for(String userId : addUserIdList){
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(userId);

                BeanUtil.setCreateUser(currentUser, userRole);
                BeanUtil.setUpdateUser(currentUser, userRole);

                this.sysUserRoleMapper.insert(userRole);
            }
        }
    }
}
