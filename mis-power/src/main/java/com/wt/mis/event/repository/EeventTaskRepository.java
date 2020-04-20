package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.EeventTask;
import org.springframework.stereotype.Repository;

@Repository
public interface EeventTaskRepository extends BaseRepository<EeventTask,Long> {
}
