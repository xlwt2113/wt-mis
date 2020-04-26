package com.wt.mis.sms.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sms.entity.Mobile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileRepository extends BaseRepository<Mobile,Long> {

    /**
     * 根据账号及台区获取数据条数
     * @param del
     * @param accountId
     * @param transformId
     * @return
     */
    int countAllByDelAndAccountIdAndTransformId(int del,long accountId ,long transformId);


    /**
     * 获取某个台区下所有的短信接收人员列表
     * @param del
     * @param transfromId
     * @return
     */
    List<Mobile> findAllByDelAndTransformId(int del, long transfromId);
}
