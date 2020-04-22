package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.EventTask;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EeventTaskRepository extends BaseRepository<EventTask,Long> {

    List<EventTask> findAllByDelAndAccountId(int del, long accountId);
}
