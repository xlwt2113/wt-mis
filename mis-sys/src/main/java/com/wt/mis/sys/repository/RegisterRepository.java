package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Register;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRepository extends BaseRepository<Register,Long> {

    public Register  getRegisterByItemName(String itemName);
}
