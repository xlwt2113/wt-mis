package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.PowerOutage;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerOutageRepository extends BaseRepository<PowerOutage,Long> {
}
