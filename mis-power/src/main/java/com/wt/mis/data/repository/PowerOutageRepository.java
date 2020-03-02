package com.wt.mis.data.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.data.entity.PowerOutage;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerOutageRepository extends BaseRepository<PowerOutage,Long> {
}
