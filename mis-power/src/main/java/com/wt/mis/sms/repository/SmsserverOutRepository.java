package com.wt.mis.sms.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.sms.entity.SmsserverOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsserverOutRepository  extends JpaRepository<SmsserverOut, Long>, JpaSpecificationExecutor<SmsserverOut> {
}
