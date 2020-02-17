package com.wt.mis.test.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.test.entity.TestComputer;
import org.springframework.stereotype.Repository;

@Repository
public interface TestComputerRepository extends BaseRepository<TestComputer,Long> {
}
