package com.wt.mis.sys.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sys.entity.Dict;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictRepository extends BaseRepository<Dict, Long> {

    Dict getFirstByDictNameAndDel(String dictName,int del);

    List<Dict> getAllByDel(int del);

}
