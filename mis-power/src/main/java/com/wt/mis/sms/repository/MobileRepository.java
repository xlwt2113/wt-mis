package com.wt.mis.sms.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sms.entity.Mobile;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileRepository extends BaseRepository<Mobile,Long> {
}
