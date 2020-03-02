package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends BaseRepository<Notification,Long> {
}
