package com.wt.mis.fi.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.fi.entity.DaySms;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaySmsRepository extends BaseRepository<DaySms,Long> {

    List<DaySms> findAllBySendTimeAndDel(String sendTime, int del);
}
