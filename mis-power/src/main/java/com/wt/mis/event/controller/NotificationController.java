
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/event/notification")
public class NotificationController extends BaseController<Notification> {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public BaseRepository<Notification, Long> repository() {
        return notificationRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "event/notification";
    }

    @Override
    protected String generateSearchSql(Notification notification, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from event_notification as t1  where t1.del = 0 ");
        return sql.toString();
    }

    /**
     * 处理列表页面中要显示的数据内容
     *
     * @param searchResultlist
     */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for (Object obj : searchResultlist) {
            HashMap<String, String> map = (HashMap) obj;
            String key = "";
            key = DictUtils.getDictItemKey("设备类型", map.get("dev_type"));
            map.replace("dev_type", key);
            key = DictUtils.getDictItemKey("事件类型", map.get("event_type"));
            map.replace("event_type", key);
            key = DictUtils.getDictItemKey("事件状态", map.get("event_status"));
            map.replace("event_status", key);
            key = DictUtils.getDictItemKey("事件接收处理方", map.get("event_receiver"));
            map.replace("event_receiver", key);
        }
    }

}