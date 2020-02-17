package com.wt.mis.core.service;

import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.repository.BaseRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author mac
 */
public interface BaseCurdService<T extends BaseEntity> {


    /**
     * 返回数据源
     *
     * @return
     * @author mac
     * @date 2018/6/12 21:50
     */
    BaseRepository<T, Long> repository();

    /**
     * 创建
     *
     * @param t
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default T save(T t) {
        return repository().save(t);
    }

    /**
     * 保存多个
     *
     * @param var
     * @author mac
     * @date 2018/6/12 22:49
     */
    default void saveAll(Iterable<? extends T> var) {
        repository().saveAll(var);
    }

    /**
     * 删除
     *
     * @param t
     * @author mac
     * @date 2018/6/12 22:49
     */
    default void delete(T t) {
        repository().delete(t);
    }

    /**
     * 删除
     *
     * @param id
     * @author mac
     * @date 2018/6/12 22:49
     */
    default void deleteById(Long id) {
        repository().deleteById(id);
    }

    /**
     * 逻辑删除一条记录
     *
     * @param id
     */
    default void deleteByIdOnLogic(Long id) {
        repository().deleteByIdOnLogic(id);
    }


    /**
     * 删除所有
     *
     * @param list
     * @author mac
     * @date 2018/6/12 22:49
     */
    default void deleteAll(Iterable<? extends T> list) {
        repository().deleteAll(list);
    }

    /**
     * 删除所有
     * 通过id
     *
     * @param ids
     */
    default void deleteAllByIds(Iterable<Long> ids) {
        repository().deleteAllByIds(ids);
    }

    /**
     * 逻辑删除所有
     * 通过id
     *
     * @param ids
     */
    default void deleteAllByIdsOnLogic(Iterable<Long> ids) {
        repository().deleteAllByIdsOnLogic(ids);
    }

    /**
     * 修改
     *
     * @param input
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default T update(T input) {
        Assert.notNull(input.getId(), "无法更新未实例化的对象！");
        return repository().save(input);
    }

    /**
     * 查询
     *
     * @param example
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default T find(Example<T> example) {
        return repository().findOne(example).orElse(null);
    }

    /**
     * 查询
     *
     * @param id
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default T findById(Long id) {
        return repository().findById(id).orElse(null);
    }

    /**
     * 查询所有
     *
     * @param example
     * @param pageable
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default Page<T> findAll(Example<T> example, Pageable pageable) {
        return repository().findAll(example, pageable);
    }

    /**
     * 查询所有
     *
     * @param spec
     * @param pageable
     * @return
     */
    default Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return repository().findAll(spec, pageable);
    }

    /**
     * 查询所有
     *
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default List<T> findAll(Example<T> example) {
        return repository().findAll(example);
    }

    /**
     * 查询所有
     *
     * @return
     * @author mac
     * @date 2018/6/12 22:49
     */
    default List<T> findAll() {
        return repository().findAll();
    }

    /**
     * 查询所有id
     *
     * @param ids
     * @return
     */
    default List<T> findAllByIds(Iterable<Long> ids) {
        return repository().findAllByIds(ids);
    }
}
