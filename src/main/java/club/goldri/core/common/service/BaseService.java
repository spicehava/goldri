package club.goldri.core.common.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用接口
 */
@Service
public interface BaseService<T> {

    T getByKey(Object key);

    String save(T entity);

    int remove(Object key);

    int updateAll(T entity);

    int updateNotNull(T entity);

    List<T> listByExample(Object example);

    //TODO 其他...
}
