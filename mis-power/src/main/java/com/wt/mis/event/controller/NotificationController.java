
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.repository.TopologyRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/event/notification")
public class NotificationController extends BaseController<Notification> {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    TopologyRepository topologyRepository;

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
        //只获取需要前置机处理的事件通知
        StringBuffer sql = new StringBuffer("select t1.*,t2.dev_name from event_notification as t1 left join dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type  where t1.del = 0  and t1.event_receiver = 2");
        if(StringUtils.isNotEmpty(request.getParameter("devType"))){
            sql.append(" and t1.dev_type = " + request.getParameter("devType"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("devName"))){
            sql.append(" and t2.dev_name like '%"+request.getParameter("devName")+"%'");
        }
        sql.append(" order by create_time desc ");
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
        String freezeTime = request.getParameter("freezeTime");
        String eventValue = request.getParameter("eventValue");
        //默认的事件执行优先级，用户手动点击的事件优先级
        int eventPriority = 5;
        if(StringUtils.isNotEmpty(request.getParameter("seq"))){
            eventPriority = Integer.parseInt(request.getParameter("seq"));
        }
        int cnt = 0;
        //获取正在执行的事件数量
        if(eventType == 9){
            //添加设备需要根据发送的命令内容获取名下数量
            cnt = notificationRepository
                    .countAllByDelAndEventValueAndEventStatusIsLessThanEqual(0,eventValue,1);
        }else if(eventType == 10) {
            //删除设备
            //删除时先判断该设备通讯地址在拓扑中是否存在，部存在提示用户修改
            int exitsDevCnt = topologyRepository.countAllByDelAndDevAddress(0,eventValue);
            if(exitsDevCnt==0){
                return ResponseUtils.ok("通讯地址不存在，请检查！");
            }
        }else{
            //判断设备是否在线，不在线不继续进行下一步处理
            List<Topology> devList = topologyRepository.findAllByDelAndDevIdAndDevType(0,Long.parseLong(devId),Integer.parseInt(devType));
            for(Topology dev : devList){
                if(dev.getDevOnline()==1){
                    return ResponseUtils.ok("设备离线，无法发送命令");
                }
            }
            //如果是获取冻结数据，需要判断获取日期是否是当日，如果是当日，提示无法召测
            if(DateUtils.dateFormat(new Date()).equals(freezeTime) && eventType == 4){
                return ResponseUtils.ok("当日尚未生成冻结数据，无法召测");
            }

            if(StringUtils.isNotEmpty(devId)){
                //根据设备ID及类型以及事件类型获取正在执行的事件数量
                cnt = notificationRepository
                        .countAllByDelAndDevIdAndDevTypeAndEventTypeAndEventStatusIsLessThanEqual(0,Long.parseLong(devId),Integer.parseInt(devType),eventType,1);
            }else{
                //根据事件类型获取正在执行的事件数量（有的事件不一定传递设备信息）
                cnt = notificationRepository
                        .countAllByDelAndEventTypeAndEventStatusIsLessThanEqual(0,eventType,1);
            }
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


            //冻结数据的召测日期格式需要处理下
            if(eventType==4){
                //召测冻结数据存入召测日期
                notification.setEventValue(freezeTime.replace("-","/"));
            }else{
                notification.setEventValue(eventValue);
            }

            notification.setEventType(eventType);
            //事件状态设置为初始状态为未处理
            notification.setEventStatus(0);
            //设置事件接收端为前置机
            notification.setEventReceiver(2);
            //事件处理优先级，默认为5
            notification.setEventPriority(eventPriority);
            notificationRepository.save(notification);
            return ResponseUtils.ok("命令发送成功！");
        }else {
            return ResponseUtils.ok("命令正在执行中，请勿重新发送！");
        }
    }

}