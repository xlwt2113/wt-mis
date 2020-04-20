package com.wt.mis.event.taskrun;

import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.DevModel;
import com.wt.mis.event.entity.EeventTask;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.repository.EeventTaskRepository;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.repository.AccountRepository;
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

    /**
     * 定时任务执行方法
     */
    @Scheduled(cron = "0/10 * * * * ?")
    private void configureTasks() {
        log.info("========定时任务启动...");
        this.dealNotificationSend();
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
            List<Account> accountList = accountRepository.findAllByDelAndDepId(0,dev.getOperationsTeam());
            for(Account account : accountList){
                EeventTask task = this.generateTask(notification,dev);
                task.setAccountId(account.getId());
                eeventTaskRepository.save(task);
            }
            //事件通知已经处理
            notification.setDealStatus(1);
            notificationRepository.save(notification);
        }
    }

    /**
     * 对主站接收到的命令进行处理
     */
    private  void dealNotificationReceive(){

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
    private EeventTask generateTask(Notification notification,DevModel dev){
        EeventTask task = new EeventTask();
        String eventName = DictUtils.getDictItemKey("事件类型",String.valueOf(notification.getEventType()));
        String devTypeName = DictUtils.getDictItemKey("设备类型",String.valueOf(dev.getDevType()));
        task.setMsg("对 "+ dev.getDevName() + "（"+devTypeName+"） 执行的【"+eventName+"】命令已经处理成功！<a href=\"#\">不再提示</a>");
        return task;
    }
}
