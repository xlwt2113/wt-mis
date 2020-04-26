package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.PowerOutage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PowerOutageRepository extends BaseRepository<PowerOutage,Long> {

//    List<PowerOutage> getAllByDelAndHistoryAndPowerStatus(int del,int history,int powerStatus);
//
//    int countAllByDelAndHistoryAndPowerStatus(int del,int history,int powerStatus);

    List<PowerOutage> findAllByDelAndDevIdAndDevTypeAndHistoryOrderByCreateTimeDesc(int del,long devId,int devType,int history);

}
