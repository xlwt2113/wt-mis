package com.wt.mis.event.taskrun;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.DevModel;
import com.wt.mis.event.entity.EventTask;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.repository.EeventTaskRepository;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.repository.AccountRepository;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class EventTaskRun {

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    DevService devService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EeventTaskRepository eeventTaskRepository;
    @Autowired
    SearchService searchService;
    @Autowired
    DepRespository depRespository;

    /**
     * 定时任务执行方法
     */
    @Scheduled(cron = "0/10 * * * * ?")
    private void configureTasks() {
        log.info("========定时任务启动...");
        this.dealNotificationSend();
        this.dealNotificationReceive();
        log.info("========定时任务结束...");
    }

    /**
     * 对主站发出的命令进行处理
     */
    private void dealNotificationSend(){
        List<Notification> list = notificationRepository.findAllSendNodificationList();
        for(Notification notification:list){
            log.info("======获取到了发出的命令"+notification.getEventStatus().toString());
            //根据设备所属台区，将通知信息逐一发送给管理该台区的每个人
            DevModel dev = devService.getDevModel(notification.getDevId(),notification.getDevType());
            EventTask task = this.generateTask(notification,dev);
            task.setAccountId(notification.getAccountId());
            eeventTaskRepository.save(task);
            //事件通知已经处理
            notification.setDealStatus(1);
            notificationRepository.save(notification);
        }
    }

    /**
     * 对主站接收到的命令进行处理
     */
    private  void dealNotificationReceive(){
        //目前接收到的主要为报警通知，接收到报警通知后，从报警表中获取报警数据写入通知信息表中
        List<Notification> list = notificationRepository.findAllReceivedNodificationList();
        for(Notification notification:list){
            //先更新消息通知的处理状态
            notification.setDealStatus(1);
            notificationRepository.save(notification);
            //删除已经存在的通知信息
        }
    }


    /**
     * 根据设备id及类型获取管理该设备的人员信息
     * @param devId
     * @param devType
     * @return
     */
    private List<Account> getAllAccountByDev(long devId,int devType){
        DevModel dev = devService.getDevModel(devId,devType);
        return accountRepository.findAllByDelAndDepId(0,dev.getOperationsTeam());
    }

    /**
     *  根据事件通知表生成消息通知内容等信息
     * @param notification
     * @return
     */
    private EventTask generateTask(Notification notification, DevModel dev){
        EventTask task = new EventTask();
        String eventName = DictUtils.getDictItemKey("事件类型",String.valueOf(notification.getEventType()));
        String devTypeName = DictUtils.getDictItemKey("设备类型",String.valueOf(dev.getDevType()));
        StringBuffer msg = new StringBuffer("");
        msg.append("对 "+ dev.getDevName() + "（"+devTypeName+"） 执行的【"+eventName+"】命令已经处理成功");
        if(StringUtils.isNotEmpty(notification.getEventValue())){
            msg.append(",返回结果为："+notification.getEventValue());
        }
        msg.append("<br><br><a href=\"#\" style=\"color:blue\" class=\"task_notice\" val=\"1\">点击这里不再提示</a>");
        return task;
    }
}
