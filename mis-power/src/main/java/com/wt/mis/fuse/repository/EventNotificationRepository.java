package com.wt.mis.fuse.repository;

import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.fuse.entity.EventNotification;
import com.wt.mis.fuse.entity.HistoryData;
import com.wt.mis.sys.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNotificationRepository extends BaseRepository<EventNotification,Long> {

    /**
     * 获取某个设备当前执行的实际
     * @return
     */
    @Query(value = "from EventNotification where del =0 and eventReceiver = 2 and eventStatus in (0,1) and hubId = ?1 and eventType = ?2")
    List<EventNotification> findRuningEvent(Long hubId,Integer eventType);

}
