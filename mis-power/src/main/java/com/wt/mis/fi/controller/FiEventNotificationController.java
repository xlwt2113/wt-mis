
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.FiEventNotification;
import com.wt.mis.fi.repository.FiEventNotificationRepository;
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

@Slf4j
@Controller
@RequestMapping("/fi/notification")
public class FiEventNotificationController extends BaseController<FiEventNotification> {

    @Autowired
    FiEventNotificationRepository fiEventNotificationRepository;

    @Override
    public BaseRepository<FiEventNotification, Long> repository() {
        return fiEventNotificationRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/notification";
    }

    @Override
    protected String generateSearchSql(FiEventNotification fiEventNotification, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fi_fi_event_notification as t1  where t1.del = 0 ");
        return sql.toString();
    }

    @ApiOperation("主站与前置机事件通讯接口")
    @GetMapping("/send_event/{hubId}/{eventType}")
    @ResponseBody
    public ResponseEntity sendEvent(HttpServletRequest request, @PathVariable long hubId, @PathVariable int eventType){
        int cnt = fiEventNotificationRepository.countAllByDelAndHubIdAndEventTypeAndEventReceiverAndEventStatusLessThan(0,hubId,eventType,2,2);
        if(cnt>0){
            return ResponseUtils.ok("命令正在执行中，请勿重新发送！");
        }else{
            FiEventNotification event = new FiEventNotification();
            event.setHubId(hubId);
            event.setEventReceiver(2); //接收方为前置机
            event.setEventStatus(0); //已发送命令尚未执行
            event.setEventType(eventType);
            event.setDel(0);
            fiEventNotificationRepository.save(event);
            return ResponseUtils.ok("命令发送成功！");
        }
    }


}
