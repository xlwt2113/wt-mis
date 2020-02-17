package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {

    /**
     * 根据用户名，账号状态及删除标记查找用户
     * @param name
     * @param status
     * @param del
     * @return
     */
    Account findByNameAndStatusAndDel(String name, int status, int del);

    /**
     * 根据用户名及删除标记查找用户
     * @param name
     * @param del
     * @return
     */
    Account findByNameAndDel(String name, int del);

    /***
     * 根据用户名及删除标记查找ID不等于指定值的用户
     * @param name
     * @param del
     * @param id
     * @return
     */
    Account findByNameAndDelAndIdNot(String name, int del, Long id);
}
