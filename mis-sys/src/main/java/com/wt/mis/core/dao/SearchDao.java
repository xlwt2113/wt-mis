package com.wt.mis.core.dao;

import com.wt.mis.core.entity.BaseEntity;

/**
 * 通用的持久对象处理的DAO
 *
 * @author 王涛
 */
public interface SearchDao {


    /**
     * 通过sql获取数据对象
     *
     * @param obj        返回对应数据库的实体类
     * @param sql
     * @param pageNumber 当前页
     * @param pageSize   每月显示记录数
     * @return
     */
    PageResult findBySql(BaseEntity obj, String sql, int pageNumber, int pageSize);


    /**
     * 通过sql获取数据对象
     *
     * @param sql
     * @param pageNumber 当前页
     * @param pageSize   每月显示记录数
     * @return
     */
    PageResult findBySql(String sql, int pageNumber, int pageSize);


    /**
     * 根据sql获取数据对象，list对象为obj
     * @param sql
     * @return
     */
    PageResult findAllBySql(String sql);


}
