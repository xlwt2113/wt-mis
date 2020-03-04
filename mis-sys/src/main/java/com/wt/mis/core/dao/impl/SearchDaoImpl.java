package com.wt.mis.core.dao.impl;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.dao.SearchDao;
import com.wt.mis.core.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author mac
 */
@Repository
@Slf4j
public class SearchDaoImpl implements SearchDao {

    /**
     * 通过此注解注入的em不应该close
     */
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult findBySql(BaseEntity obj, String sql, int pageNumber, int pageSize) {
        PageResult p = new PageResult();
        p.setPageSize(pageSize);
        p.setPageNumber(pageNumber);
        try {
            Query query = entityManager.createNativeQuery(sql, obj.getClass())
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize);
            p.setContent(query.getResultList());
            sql = "select count(*) as cnt from (" + sql + ") as t2";
            query = entityManager.createNativeQuery(sql);
            p.setTotalElements(Integer.valueOf(query.getResultList().get(0).toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("------------分页错误---------------");
        }

        return p;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult findBySql(String sql, int pageNumber, int pageSize) {
        PageResult p = new PageResult();
        p.setPageSize(pageSize);
        p.setPageNumber(pageNumber);
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.unwrap(NativeQueryImpl.class)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize);
            p.setContent(query.getResultList());

            sql = "select count(*) as cnt from (" + sql + ") as t2";
            query = entityManager.createNativeQuery(sql);
            p.setTotalElements(Integer.valueOf(query.getResultList().get(0).toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("------------分页错误---------------");
        }
        return p;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PageResult findAllBySql(String sql) {
        PageResult p = new PageResult();
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.unwrap(NativeQueryImpl.class)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            p.setContent(query.getResultList());
            sql = "select count(*) as cnt from (" + sql + ") as temp";
            query = entityManager.createNativeQuery(sql);
            p.setTotalElements(Integer.valueOf(query.getResultList().get(0).toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return p;
    }
}
