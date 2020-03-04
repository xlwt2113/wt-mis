package com.wt.mis.data.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.data.entity.RealTime;
import org.springframework.stereotype.Repository;

@Repository
public interface RealTimeRepository extends BaseRepository<RealTime,Long> {
}
