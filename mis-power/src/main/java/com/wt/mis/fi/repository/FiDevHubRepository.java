package com.wt.mis.fi.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.fi.entity.FiDevHub;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiDevHubRepository extends BaseRepository<FiDevHub,Long> {

    List<FiDevHub> findAllByLineIdAndParentIdAndDel(long lineId, long pid, int del);

    List<FiDevHub> findAllByLineIdAndDel(long lineId, int del);
}
