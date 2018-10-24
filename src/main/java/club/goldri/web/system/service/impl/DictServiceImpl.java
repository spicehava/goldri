package club.goldri.web.system.service.impl;

import club.goldri.core.util.StringUtil;
import com.github.pagehelper.PageHelper;
import club.goldri.core.common.service.AbstractService;
import club.goldri.core.constant.Constant;
import club.goldri.core.util.BeanUtil;
import club.goldri.core.util.SystemCacheUtil;
import club.goldri.web.system.domain.SysDict;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.mapper.SysDictMapper;
import club.goldri.web.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DictServiceImpl extends AbstractService<SysDict> implements DictService {
    @Autowired
    private SysDictMapper sysDictMapper;

    /**
     * 查询方法，支持模糊查询
     *
     * @param sysDict
     * @return
     */
    public List<SysDict> listAll(SysDict sysDict){
        if (sysDict.getPage() != null && sysDict.getRows() != null) {
            PageHelper.startPage(sysDict.getPage(), sysDict.getRows());
        }

        Example example = new Example(SysDict.class);
        Example.Criteria criteria = example.createCriteria();

        if(sysDict != null && sysDict.getLabel() != null){
            criteria.andLike("label","%" + sysDict.getLabel() + "%");
        }

        if(sysDict != null && sysDict.getType() != null){
            criteria.andEqualTo("type", sysDict.getType());
        }

        if(sysDict != null && sysDict.getCode() != null){
            criteria.andEqualTo("code", sysDict.getCode());
        }

        if(sysDict != null && sysDict.getDescription() != null){
            criteria.andEqualTo("description", sysDict.getDescription());
        }

        //获取删除标记为正常的记录
        criteria.andEqualTo("delFlag", sysDict.getDelFlag());

        return this.sysDictMapper.selectByExample(example);
    }

    /**
     * 将修改和添加封装在一起，便于缓存处理
     * （更新缓存）
     *
     * @param currentUser
     * @param sysDict
     * @return
     */
    @CachePut(value = SystemCacheUtil.CACHE_DICT,
            key = "T(club.goldri.core.util.SystemCacheUtil).SYS_DICT_TC + #sysDict.type + #sysDict.code",
            condition = "#sysDict.type != null and #sysDict.code != null")
    @Override
    public SysDict commonSave(SysUser currentUser, SysDict sysDict) {
        SysDict dict = new SysDict();
        dict.setType(sysDict.getType());
        dict.setCode(sysDict.getCode());

        List<SysDict> dictList = this.listAll(dict);

        if(dictList == null || dictList.size() == 0){
            BeanUtil.setCreateUser(currentUser, sysDict);
            BeanUtil.setUpdateUser(currentUser, sysDict);
            if(StringUtil.isEmpty(sysDict.getIsDesign())){
                sysDict.setIsDesign("0");
            }

            if(sysDict.getSort() == null){
                Integer maxSort = this.sysDictMapper.selectMaxSort(sysDict.getType());
                if(maxSort == null){
                    sysDict.setSort(new Integer((10)));
                } else {
                    String maxSortStr = maxSort.toString();
                    if(maxSortStr.lastIndexOf("0") == -1){
                        maxSortStr = maxSortStr.substring(0, maxSortStr.length() - 1) + "0";
                    }
                    sysDict.setSort(new Integer(maxSortStr) + 10);
                }
            }
            this.save(sysDict);
        } else if(dictList.size() == 1){
            Example example = new Example(SysDict.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("type", sysDict.getType());
            criteria.andEqualTo("code", sysDict.getCode());
            criteria.andEqualTo("delFlag", Constant.DEL_FLAG_NORMAL);

            BeanUtil.setUpdateUser(currentUser, sysDict);
            this.sysDictMapper.updateByExampleSelective(sysDict, example);
        } else {
            return null;
        }

        return this.selectDictByTypeAndCode(sysDict.getType(), sysDict.getCode());
    }

    /**
     * 查询单个字典
     *
     * @param type
     * @param code
     * @return
     */
    @Cacheable(value = SystemCacheUtil.CACHE_DICT, key = "T(club.goldri.core.util.SystemCacheUtil).SYS_DICT_TC + #type + #code")
    @Override
    public SysDict selectDictByTypeAndCode(String type, String code) {
        SysDict sysDict = new SysDict();
        sysDict.setPage(0);//不分页
        sysDict.setType(type);
        sysDict.setCode(code);
        List<SysDict> dictList = this.listAll(sysDict);
        if(dictList != null && dictList.size() == 1){
            return dictList.get(0);
        }
        return null;
    }

    /**
     * 软删除单个字典（清除缓存）
     *
     * @param currentUser
     * @param type
     * @param code
     * @return
     */
    @CacheEvict(value = SystemCacheUtil.CACHE_DICT, allEntries = true)
    @Override
    public SysDict removeDictByTypeAndCode(SysUser currentUser, String type, String code) {
        SysDict sysDict = new SysDict();
        sysDict.setType(type);
        sysDict.setCode(code);
        BeanUtil.setUpdateUser(currentUser, sysDict);
        sysDict.setDelFlag(Constant.DEL_FLAG_DELETE);

        this.updateNotNull(sysDict);
        return this.selectDictByTypeAndCode(type, code);
    }

    /**
     * 根据type查询
     *
     * @param type
     * @return
     */
    @Cacheable(value = SystemCacheUtil.CACHE_DICT, key = "T(club.goldri.core.util.SystemCacheUtil).SYS_DICT_TYPE + #type")
    @Override
    public List<SysDict> listByType(String type) {
        SysDict sysDict = new SysDict();
        sysDict.setRows(0);//不分页
        sysDict.setType(type);
        return this.listAll(sysDict);
    }
}
