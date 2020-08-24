package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 根据部门id获取该部门下的所有人员
     * @param del
     * @param depId
     * @return
     */
    List<Account> findAllByDelAndDepId(int del, long depId);

    @Query(value = "select t1 from Account as t1 left join Dep as t2 on t1.depId = t2.id where t1.del=0 and t2.level like '_?1%'")
    List<Account> findAllByDepLevel(String depLevel);



}
