package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Dep;
import org.springframework.stereotype.Repository;

@Repository
public interface DepRespository extends BaseRepository<Dep, Long> {
}
