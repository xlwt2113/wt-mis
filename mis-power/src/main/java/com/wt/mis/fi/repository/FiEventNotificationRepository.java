package com.wt.mis.fi.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.fi.entity.FiEventNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiEventNotificationRepository extends BaseRepository<FiEventNotification,Long> {

    //获取主站向前置机发送通知的执行状态
    int countAllByDelAndHubIdAndEventTypeAndEventReceiverAndEventStatusLessThan(int del,long hubId,int eventType,int eventReceiver,int eventStatus);


    /**
     * 根据事件类型及接受方获取信息通知列表
     * @param del
     * @param eventType
     * @param eventReceiver
     * @return
     */
    List<FiEventNotification> getAllByDelAndEventTypeAndEventReceiverAndEventStatus(int del,int eventType,int eventReceiver,int eventStatus);
}
