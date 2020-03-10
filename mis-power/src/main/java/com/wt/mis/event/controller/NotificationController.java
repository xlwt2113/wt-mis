
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @param searchResultlist
     */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for (Object obj : searchResultlist) {
            HashMap<String, String> map = (HashMap) obj;
            String key = "";
            key = DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_type")));
            map.replace("dev_type", key);
            key = DictUtils.getDictItemKey("事件类型", String.valueOf(map.get("event_type")));
            map.replace("event_type", key);
            key = DictUtils.getDictItemKey("事件状态", String.valueOf(map.get("event_status")));
            map.replace("event_status", key);
            key = DictUtils.getDictItemKey("事件接收处理方", String.valueOf(map.get("event_receiver")));
            map.replace("event_receiver", key);
        }
    }

    /**
     * 主站发送根据传递的事件类型调用不同的事件处理机制，不同事件的参数根据情况在request中获取
     * @param eventType 事件类型，共有20个
     * @return
     */
    @ApiOperation("主站与前置机事件通讯接口")
    @GetMapping("/send_event/{eventType}")
    @ResponseBody
    public ResponseEntity sendEvent(HttpServletRequest request,@PathVariable int eventType){
        String devId = request.getParameter("devId");
        String devType = request.getParameter("devType");
        int cnt = 0;
        //获取正在执行的事件数量
        if(StringUtils.isNotEmpty(devId)){
            //根据设备ID及类型以及事件类型获取正在执行的事件数量
            cnt = notificationRepository
                    .countAllByDelAndDevIdAndDevTypeAndEventTypeAndEventStatusIsLessThanEqual(0,Long.parseLong(devId),Integer.parseInt(devType),eventType,1);
        }else{
            //根据事件类型获取正在执行的事件数量（有的事件不一定传递设备信息）
            cnt = notificationRepository
                    .countAllByDelAndEventTypeAndEventStatusIsLessThanEqual(0,eventType,1);
        }
        //当前没有正在执行事件的任务时，才能新建事件通知
        if(cnt==0){
            Notification notification = new Notification();
            //写入设备ID
            if(StringUtils.isNotEmpty(devId)){
                notification.setDevId(Long.parseLong(request.getParameter("devId")));
            }
            //写入设备类型
            if(StringUtils.isNotEmpty(devType)){
                notification.setDevType(Integer.parseInt(request.getParameter("devType")));
            }
            notification.setEventType(eventType);
            //事件状态设置为初始状态为未处理
            notification.setEventStatus(0);
            //设置事件接收端为前置机
            notification.setEventReceiver(2);
            notificationRepository.save(notification);
            return ResponseUtils.ok("命令发送成功！");
        }else {
            return ResponseUtils.ok("命令正在执行中，请勿重新发送！");
        }
    }

}