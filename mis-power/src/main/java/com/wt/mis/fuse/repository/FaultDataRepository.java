package com.wt.mis.fuse.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.fuse.entity.FaultData;
import org.springframework.stereotype.Repository;

@Repository
public interface FaultDataRepository extends BaseRepository<FaultData,Long> {
}
