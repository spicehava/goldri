package club.goldri.web.system.service.impl;

import club.goldri.core.constant.Constant;
import club.goldri.core.util.BeanUtil;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.domain.SysUserOrg;
import club.goldri.web.system.mapper.SysOrgMapper;
import com.github.pagehelper.PageHelper;
import club.goldri.core.common.service.AbstractService;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysOrg;
import club.goldri.web.system.mapper.SysUserOrgMapper;
import club.goldri.web.system.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.List;

@Service
public class OrgServiceImpl extends AbstractService<SysOrg> implements OrgService {

    @Autowired
    private SysOrgMapper sysOrgMapper;

    @Autowired
    private SysUserOrgMapper sysUserOrgMapper;
    /**
     * 查询方法，支持模糊查询
     *
     * @param sysOrg
     * @return
     */
    @Override
    public List<SysOrg> listAll(SysOrg sysOrg) {
        if (sysOrg.getPage() != null && sysOrg.getRows() != null) {
            PageHelper.startPage(sysOrg.getPage(), sysOrg.getRows());
        }

        Example example = new Example(SysOrg.class);
        Example.Criteria criteria = example.createCriteria();

        if (sysOrg != null && sysOrg.getName() != null) {
            criteria.andLike("name", "%" + sysOrg.getName() + "%");
        }
        if (sysOrg != null && sysOrg.getId() != null) {
            criteria.andEqualTo("id", sysOrg.getId());
        }
        if (sysOrg != null && sysOrg.getParentId() != null) {
            criteria.andEqualTo("parentId", sysOrg.getParentId());
        }
        //TODO other search params

        //获取删除标记为正常的记录
        criteria.andEqualTo("delFlag", sysOrg.getDelFlag());

        return this.sysOrgMapper.selectByExample(example);
    }

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param currentUser
     * @param sysOrg
     * @return
     */
    @CachePut(value = SystemCacheUtil.CACHE_ORG, key = "#sysOrg.id", condition = "#sysOrg.id != null")
    @CacheEvict(value = SystemCacheUtil.CACHE_USER, allEntries = true)
    @Override
    public SysOrg commonSave(SysUser currentUser, SysOrg sysOrg) {
        if (sysOrg.getId() == null) {
            this.save(sysOrg);
        } else {
            this.updateNotNull(sysOrg);
        }

        return this.selectOrgById(sysOrg.getId());
    }

    /**
     * 查询单个组织（根据需要封装子记录）
     *
     * @param id
     * @return
     */
    @Cacheable(value = SystemCacheUtil.CACHE_ORG, key = "#id")
    @Override
    public SysOrg selectOrgById(String id) {
        SysOrg sysOrg = null;

        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",id);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

        List<SysOrg> orgList = this.listByExample(example);
        if(orgList.size() == 1){
            sysOrg = orgList.get(0);
        }
        //TODO 如果需要封装记录可以在这里实现

        return sysOrg;
    }

    /**
     * 软删除单个组织（清除缓存）
     *
     * @param currentUser
     * @param id
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = SystemCacheUtil.CACHE_ORG, key = "#id"),
            @CacheEvict(value = SystemCacheUtil.CACHE_USER, allEntries = true)
    })
    @Override
    public SysOrg removeOrgById(SysUser currentUser, String id) {
        SysOrg sysOrg = new SysOrg();
        sysOrg.setId(id);
        sysOrg.setDelFlag(Constant.DEL_FLAG_DELETE);
        BeanUtil.setUpdateUser(currentUser, sysOrg);
        sysOrg = this.commonSave(currentUser, sysOrg);

        //删除用户组织关系（软删除）
        SysUserOrg sysUserOrg = new SysUserOrg();
        sysUserOrg.setDelFlag(Constant.DEL_FLAG_DELETE);

        Example example = new Example(SysUserOrg.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("orgId",id);
        criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);
        this.sysUserOrgMapper.updateByExampleSelective(sysUserOrg,example);
        return sysOrg;
    }
}
