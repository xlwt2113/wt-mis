package com.wt.mis.event.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification,Long> {

    int countAllByDelAndDevIdAndDevTypeAndEventTypeAndEventStatusIsLessThanEqual(int del,long devId,int devType,int eventType,int eventStatus);

    int countAllByDelAndEventValueAndEventStatusIsLessThanEqual(int del,String eventValue,int eventStatus);

    int countAllByDelAndEventTypeAndEventStatusIsLessThanEqual(int del,int eventType,int eventStatus);

    /**
     * 获取所有主站发送的已经成功执行的事件列表
     * @return
     */
    @Query("from Notification where del =0 and eventReceiver = 2 and eventStatus in (3,4) and dealStatus = 0")
    List<Notification> findAllSendNodificationList();

    @Query("from Notification where del =0 and eventReceiver = 1 and eventStatus =0 and dealStatus = 0")
    List<Notification> findAllReceivedNodificationList();
}
