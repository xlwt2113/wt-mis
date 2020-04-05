package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.TimeTask;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTaskRepository extends BaseRepository<TimeTask,Long> {
}
