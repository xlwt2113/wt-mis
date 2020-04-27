package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.TransForm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransFormRepository extends BaseRepository<TransForm,Long> {

    List<TransForm> getAllByDel(int del);

    List<TransForm> getAllByOperationsTeamAndDel(long depId,int del);

    int countAllByDelAndLineId(int del,long lineId);

    List<TransForm> findAllByDelAndLineId(int del,long lineId);

    //获取某个班组下汇聚单元地址的设备数量
    int countAllByDelAndDevAddressAndOperationsTeam(int del,String devAddress,long operationsTeam);

    //获取某个班组除指定设备外，特定汇聚坦言地址的设备数量
    int countAllByDelAndDevAddressAndOperationsTeamAndIdNot(int del,String devAddress,long operationsTeam,long id);


    //获取某个班组下通讯地址的设备数量
    int countAllByDelAndProtocolAddressAndOperationsTeam(int del,String protocolAddress,long operationsTeam);

    //获取某个班组除指定设备外，特定通讯地址的设备数量
    int countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(int del,String protocolAddress,long operationsTeam,long id);
}
