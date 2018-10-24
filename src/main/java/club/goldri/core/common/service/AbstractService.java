package club.goldri.core.common.service;

import club.goldri.core.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public abstract class AbstractService<T> implements BaseService<T> {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Mapper<T> mapper;

    public Mapper<T> getMapper() {
        return mapper;
    }

    @Override
    public T getByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    public String save(T entity) {

        this.mapper.insert(entity);

        Object primaryKeyObj = ReflectUtil.getFieldValue(entity, "id");

        if(primaryKeyObj != null){
            return primaryKeyObj.toString();
        }

        return null;
    }

    public int remove(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    public int updateNotNull(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    public List<T> listByExample(Object example) {
        return mapper.selectByExample(example);
    }

    //TODO 其他...
}
