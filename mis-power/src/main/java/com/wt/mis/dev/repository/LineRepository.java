package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Line;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends BaseRepository<Line,Long> {

    List<Line> getAllByDel(int del);

    /**
     * 根据depId获取管辖下的所有线路
     * @param depId
     * @return
     */
    @Query("SELECT t FROM Line t left join Dep d on t.operationsTeam = d.id where t.del = 0 and (d.level  LIKE CONCAT('%_',?1,'%') or t.operationsTeam = ?1 )")
    List<Line> findAllByOperationsTeam(long depId);
}
