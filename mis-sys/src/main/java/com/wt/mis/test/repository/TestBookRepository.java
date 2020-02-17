package com.wt.mis.test.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.test.entity.TestBook;
import org.springframework.stereotype.Repository;

@Repository
public interface TestBookRepository extends BaseRepository<TestBook,Long> {
}
