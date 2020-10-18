package com.wt.mis.fi.taskrun;

import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.DaySms;
import com.wt.mis.fi.repository.DaySmsRepository;
import com.wt.mis.sms.entity.SmsserverOut;
import com.wt.mis.sms.repository.SmsserverOutRepository;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.repository.AccountRepository;
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
public class DaySmsTaskRun {

    @Autowired
    DaySmsRepository daySmsRepository;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    SmsserverOutRepository smsserverOutRepository;


    /**
     * 定时任务执行方法
     */
    @Scheduled(cron = "0/1 * * * * ?")
    private void configureTasks() {
        log.info("========每天的短信配置定时任务启动...");
        this.sendDaySms();
        log.info("========每天的短信配置定时任务结束...");
    }

    private void sendDaySms(){
        String msgContent;
        String currentTime = DateUtils.timeFormat(new Date());
        System.out.println("匹配时间："+currentTime);
        List<DaySms> list = daySmsRepository.findAllBySendTimeAndDel(currentTime,0);
        List sendList = new ArrayList();
        for(DaySms daySms:list){
            Account account = accountRepository.getOne(daySms.getUserId());
            msgContent = "请点击 http://218.28.178.12:18080/sms/"+DateUtils.dateFormat(new Date())+"/"+daySms.getId()+" 查看您关注的故障指示器的实时信息";
            if(StringUtils.isNotEmpty(account.getMobile())){
                //有手机号码的才发短信
                SmsserverOut out = new SmsserverOut();
                out.setType("O");
                out.setRecipient(account.getMobile());
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
                out.setText(msgContent);
                out.setOriginator("");
                sendList.add(out);
            }
        }
        //一起存储所有的带接收短信的人员
        smsserverOutRepository.saveAll(sendList);
    }
}
