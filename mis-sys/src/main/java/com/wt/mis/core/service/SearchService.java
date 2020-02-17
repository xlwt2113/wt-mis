package com.wt.mis.core.service;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.entity.BaseEntity;

public interface SearchService {


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
}
