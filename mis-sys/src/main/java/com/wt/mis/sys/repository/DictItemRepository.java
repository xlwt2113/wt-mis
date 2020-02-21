package com.wt.mis.sys.repository;


import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictItemRepository extends BaseRepository<DictItem, Long> {

    List<DictItem> getAllByDictAndDel(Dict dict,int del);

}
