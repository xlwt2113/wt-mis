package com.wt.mis.core.service.impl;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.dao.SearchDao;
import com.wt.mis.core.entity.BaseEntity;
import com.wt.mis.core.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    SearchDao searchDao;

    @Override
    public PageResult findBySql(BaseEntity obj, String sql, int pageNumber, int pageSize) {
        return searchDao.findBySql(obj, sql, pageNumber, pageSize);
    }

    @Override
    public PageResult findBySql(String sql, int pageNumber, int pageSize) {
        return searchDao.findBySql(sql, pageNumber, pageSize);
    }
}
