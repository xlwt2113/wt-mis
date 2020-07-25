package com.wt.mis.fuse.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.fuse.entity.HistoryData;
import com.wt.mis.fuse.entity.RealData;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryDataRepository extends BaseRepository<HistoryData,Long> {

}
