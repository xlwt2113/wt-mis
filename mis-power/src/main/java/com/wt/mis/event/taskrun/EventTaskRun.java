package com.wt.mis.event.taskrun;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.DevModel;
import com.wt.mis.event.entity.EventTask;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.entity.PowerOutage;
import com.wt.mis.event.repository.EeventTaskRepository;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.event.repository.PowerOutageRepository;
import com.wt.mis.sms.entity.Mobile;
import com.wt.mis.sms.entity.SmsserverOut;
import com.wt.mis.sms.repository.MobileRepository;
import com.wt.mis.sms.repository.SmsserverOutRepository;
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

import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    TopologyRepository topologyRepository;
    @Autowired
    TransFormRepository transFormRepository;
    @Autowired
    MobileRepository mobileRepository;
    @Autowired
    SmsserverOutRepository smsserverOutRepository;
    @Autowired
    PowerOutageRepository powerOutageRepository;

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
            if(dev!=null&&notification.getAccountId()!=null){
                EventTask task = this.generateTask(notification,dev);
                task.setAccountId(notification.getAccountId());
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
        //目前接收到的主要为报警通知，接收到报警通知后，从报警表中获取报警数据写入通知信息表中
        List<Notification> list = notificationRepository.findAllReceivedNodificationList();
        for(Notification notification:list){
            //停电事件的处理逻辑，停电后，给管辖的人员发送通知
            if(notification.getEventType()==1){
                List<Topology> devList = topologyRepository.findAllByDelAndDevIdAndDevType(0,notification.getDevId(),notification.getDevType());
                if(devList!=null&&devList.size()>0){
                    //标记主站已经接收到前置机的通知并进行了处理
                    notification.setDealStatus(1);
                    notification.setEventStatus(2);
                    Topology dev = devList.get(0);
                    TransForm transForm = transFormRepository.getOne(dev.getTransformId());
                    List<Mobile> mobileList = mobileRepository.findAllByDelAndTransformId(0,dev.getTransformId());
                    List<PowerOutage> powerList = powerOutageRepository.findAllByDelAndDevIdAndDevTypeAndHistoryOrderByCreateTimeDesc(0,dev.getDevId(),dev.getDevType(),0);
                    List<SmsserverOut> sendList = new ArrayList<>();
                    StringBuffer sendMsg = new StringBuffer();
                    if(powerList!=null&&powerList.size()>0){
                        PowerOutage power = powerList.get(0);
                        if(power.getPowerStatus()==0&&power.getPhaseStatus()==0&&power.getVoltageStatusA()==0&&power.getVoltageStatusB()==0&&power.getVoltageStatusC()==0){
                            //复电报警
                            sendMsg.append(transForm.getTransformName()+"【"+DictUtils.getDictItemKey("设备类型",String.valueOf(dev.getDevType()))+"】"+dev.getDevName()+"于"+DateUtils.dateFormat(notification.getCreateTime(),"yyyy/MM/dd HH:mm:ss")+" 复电，报警解除。");
                        }else{
                            //停电报警
                            sendMsg.append(transForm.getTransformName()+"【"+DictUtils.getDictItemKey("设备类型",String.valueOf(dev.getDevType()))+"】"+dev.getDevName()+"于"+DateUtils.dateFormat(notification.getCreateTime(),"yyyy/MM/dd HH:mm:ss")+" 报警。");
                            if(power.getPowerStatus()!=0){
                                sendMsg.append("停电状态："+DictUtils.getDictItemKey("停电/相序状态",String.valueOf(power.getPowerStatus()))+"；");
                            }
                            if(power.getPhaseStatus()!=0){
                                sendMsg.append("相序状态："+DictUtils.getDictItemKey("停电/相序状态",String.valueOf(power.getPhaseStatus()))+"；");
                            }
                            if(power.getVoltageStatusA()!=0){
                                sendMsg.append("A相电压："+DictUtils.getDictItemKey("电压状态",String.valueOf(power.getVoltageStatusA()))+"；");
                            }
                            if(power.getVoltageStatusB()!=0){
                                sendMsg.append("B相电压："+DictUtils.getDictItemKey("电压状态",String.valueOf(power.getVoltageStatusB()))+"；");
                            }
                            if(power.getVoltageStatusC()!=0){
                                sendMsg.append("C相电压："+DictUtils.getDictItemKey("电压状态",String.valueOf(power.getVoltageStatusC()))+"；");
                            }
                        }
                    }

                    for(Mobile mobile:mobileList){
                        //发送对象
                        Account sendUser = accountRepository.getOne(mobile.getAccountId());
                        if(StringUtils.isNotEmpty(sendUser.getMobile())){
                            SmsserverOut out = new SmsserverOut();
                            out.setType("O");
                            out.setRecipient(sendUser.getMobile());
                            out.setCreateDate(new Date());
                            out.setEncoding("U");
                            out.setStatusReport(0);
                            out.setFlashSms(0);
                            out.setSrcPort(-1);
                            out.setDstPort(-1);
                            out.setPriority(0);
                            out.setStatus("U");
                            out.setErrors(0);
                            out.setGatewayId("*");
                            out.setUserId("admin");
                            out.setText(sendMsg.toString());
                            out.setOriginator("");
                            sendList.add(out);
                        }
                    }
                    //一起存储所有的带接收短信的人员
                    smsserverOutRepository.saveAll(sendList);

                }else{
                    //未找到设备，标记状态处理出错
                    notification.setDealStatus(1);
                    notification.setEventStatus(3);
                }

                notificationRepository.save(notification);
            }
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
        eeventTaskRepository.save(task);
        String eventName = DictUtils.getDictItemKey("事件类型",String.valueOf(notification.getEventType()));
        String devTypeName = DictUtils.getDictItemKey("设备类型",String.valueOf(dev.getDevType()));
        StringBuffer msg = new StringBuffer("");
        msg.append("对 "+ dev.getDevName() + "（"+devTypeName+"） 执行的【"+eventName+"】命令已经处理成功");
        if(StringUtils.isNotEmpty(notification.getEventValue())){
            /**
             * 以下三种有返回值，需要显示返回结果
             * 12 查询汇聚单元台区号
             * 13 查询设备台区号
             * 15 查询拓扑等级
             */
            if(notification.getEventType()==12 || notification.getEventType()==13 || notification.getEventType()==15){
                msg.append(",返回结果为："+notification.getEventValue());
            }

        }
        msg.append("<br><br><a href=\"#\" style=\"color:blue\" class=\"task_notice\" val=\""+task.getId()+"\">点击这里不再提示</a>");
        task.setMsg(msg.toString());
        return task;
    }
}
