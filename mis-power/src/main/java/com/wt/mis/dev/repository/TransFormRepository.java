package com.wt.mis.dev.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.TransForm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransFormRepository extends BaseRepository<TransForm,Long> {

    List<TransForm> getAllByDel(int del);
}
