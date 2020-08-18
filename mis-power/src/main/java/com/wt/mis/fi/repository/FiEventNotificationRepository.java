package com.wt.mis.fi.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.fi.entity.FiEventNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiEventNotificationRepository extends BaseRepository<FiEventNotification,Long> {

    int countAllByDelAndHubIdAndEventTypeAndEventReceiverAndEventStatusLessThan(int del,long hubId,int eventType,int eventReceiver,int eventStatus);

}
