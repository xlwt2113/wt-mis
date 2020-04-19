package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Meter;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends BaseRepository<Meter,Long> {
    //获取某个班组下通讯地址的设备数量
    int countAllByDelAndProtocolAddressAndOperationsTeam(int del,String protocolAddress,long operationsTeam);

    //获取某个班组除指定设备外，特定通讯地址的设备数量
    int countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(int del,String protocolAddress,long operationsTeam,long id);
}
