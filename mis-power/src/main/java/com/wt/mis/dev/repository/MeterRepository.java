package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Meter;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends BaseRepository<Meter,Long> {
}
