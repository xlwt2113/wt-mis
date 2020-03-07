package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.Line;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends BaseRepository<Line,Long> {

    List<Line> getAllByDel(int del);
}
