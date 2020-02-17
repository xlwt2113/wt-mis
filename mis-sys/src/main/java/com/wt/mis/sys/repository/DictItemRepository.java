package com.wt.mis.sys.repository;


import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.DictItem;
import org.springframework.stereotype.Repository;

@Repository
public interface DictItemRepository extends BaseRepository<DictItem, Long> {

}
