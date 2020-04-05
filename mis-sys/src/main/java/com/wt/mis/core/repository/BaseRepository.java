package com.wt.mis.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 王涛
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 数据未删除标志位
     */
    int DATA_NO_DELETE = 0;
    /**
     * 数据已删除标志位
     */
    int DATA_DELETED = 1;

    /**
     * 根据删除标记获取所有数据
     *
     * @param del
     * @return
     */
    List<T> findAllByDel(int del);

    /**
     * 批量查询
     *
     * @param ids
     * @return
     */
    @Query("from #{#entityName} where id in (?1)")
    List<T> findAllByIds(Iterable<ID> ids);

    /**
     * 批量物理删除
     * - @Query 在使用 update 和 delete 语句时需要
     *
     * @param ids
     */
    @Transactional
    @Modifying
    @Query("delete from #{#entityName} where id in (?1)")
    void deleteAllByIds(Iterable<ID> ids);

    /**
     * 批量逻辑删除
     *
     * @param ids
     */
    @Transactional
    @Modifying
    @Query("update #{#entityName} set del = 1 where id in (?1)")
    void deleteAllByIdsOnLogic(Iterable<ID> ids);


    /**
     * 逻辑删除一条
     *
     * @param id
     */
    @Transactional
    @Modifying
    @Query("update #{#entityName} set del = 1 where id = ?1")
    void deleteByIdOnLogic(Long id);

}
