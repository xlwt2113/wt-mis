package com.wt.mis.fi.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.fi.entity.YxReal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YxRealRepository extends BaseRepository<YxReal,Long> {


    @Query("SELECT hubId from YxReal where inforAddr in (4,5,6,12,13,14,20,21,22,27,28,29,30,31,32,33,34,35,36,37,38) and del = 0  and yxValue = 1 group by hubId")
    List  findGroupByHubId();

}
