package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.RoleAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAccountRepository extends BaseRepository<RoleAccount, Long> {

    List<RoleAccount> findAllByAccountId(Long accountId);
}
