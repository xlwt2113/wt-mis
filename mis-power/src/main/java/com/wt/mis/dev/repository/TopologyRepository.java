package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Topology;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopologyRepository extends BaseRepository<Topology,Long> {


    List<Topology> findAllByDelAndDevType(int del,int devType);

    List<Topology> findAllByDelAndDevTypeAndDevParentId(int del,int devType,long devParentId);

    List<Topology> findAllByDelAndTransformId(int del,long transformId);

    List<Topology> findAllByDelAndDevIdAndDevType(int del,long devId,int devType);

    List<Topology> findAllByDelAndDevTypeAndTransformId(int del,int devType,long transformId);

    List<Topology> findAllByDelAndDevOnlineAndTransformId(int del,int onLine,long transformId);

}
