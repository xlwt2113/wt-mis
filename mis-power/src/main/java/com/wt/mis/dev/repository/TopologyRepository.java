package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Topology;
import org.springframework.stereotype.Repository;

@Repository
public interface TopologyRepository extends BaseRepository<Topology,Long> {
}
