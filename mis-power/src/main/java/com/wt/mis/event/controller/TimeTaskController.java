
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.entity.TimeTask;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.event.repository.TimeTaskRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/event/timetask")
public class TimeTaskController extends BaseController<TimeTask> {

    @Autowired
    TimeTaskRepository timeTaskRepository;

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<TimeTask, Long> repository() {
        return timeTaskRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "event/timetask";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, TimeTask timeTask) {

        super.initModel(id, model, timeTask);
        //获取台区列表
        List<TransForm> transFormList = transFormRepository.getAllByOperationsTeamAndDel(LoginUser.getCurrentUser().getDepId(),0);
        model.addAttribute("transFormList",transFormList);
        //初始化班组为当前登录人班组
        if(timeTask.getId()==null){
            timeTask.setDepId(LoginUser.getCurrentUser().getDepId());
            timeTask.setTaskDay(1);
            timeTask.setIntervalType(1);
        }
    }

    @Override
    protected String generateSearchSql(TimeTask timeTask, HttpServletRequest request) {

        Dep dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());

        StringBuffer sql = new StringBuffer("select t1.*,t2.transform_name, date_format(t1.task_time,'%H:%i:%s') as task_time_str,t3.name as dep_name from time_task as t1 " +
                " left join dev_transform t2 on t1.transform_id = t2.id" +
                " left join  sys_dep t3 on t3.id = t1.dep_id  where t1.del = 0  and t3.level like '" + dep.getLevel() + "%' ");
        if (timeTask.getTaskType() != null) {
            sql.append(" and t1.task_type = " + timeTask.getTaskType());
        }
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
            key = DictUtils.getDictItemKey("定时任务类型", String.valueOf(map.get("task_type")));
            map.replace("task_type", key);
            key = DictUtils.getDictItemKey("定时任务时间间隔类型", String.valueOf(map.get("interval_type")));
            map.put("interval_type_name", key);
//            key = DictUtils.getDictItemKey("台区Id", map.get("transform_id"));
//            map.replace("transform_id", key);
//            key = DictUtils.getDictItemKey("执行日", map.get("task_day"));
//            map.replace("task_day", key);
            key = DictUtils.getDictItemKey("定时任务执行状态", String.valueOf(map.get("task_state")));
            map.replace("task_state", key);
        }
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, TimeTask timeTask) {
        try {
            timeTask.setTaskTime(DateUtils.parse(DateUtils.dateFormat(new Date())+" "+timeTask.getTaskTimeStr(),"yyyy-MM-dd HH:mm:ss"));
            calculationDay(timeTask);
            this.timeTaskRepository.save(timeTask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.addEvent(timeTask);
        return ResponseUtils.okJson("新增成功", timeTask);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    protected String edit(HttpServletRequest request, TimeTask timeTask) {
        try {
            timeTask.setTaskTime(DateUtils.parse(DateUtils.dateFormat(new Date())+" "+timeTask.getTaskTimeStr(),"yyyy-MM-dd HH:mm:ss"));
            calculationDay(timeTask);
            this.timeTaskRepository.save(timeTask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.addEvent(timeTask);
        return ResponseUtils.okJson("修改成功",timeTask);
    }

    //计算下一次的任务执行时间
    private void calculationDay(TimeTask timeTask){
        try{
            if(timeTask.getTaskTime() != null){
                String year = DateUtils.dateFormat(timeTask.getTaskTime(),"yyyy");
                String month = DateUtils.dateFormat(timeTask.getTaskTime(),"MM");
                String day = DateUtils.dateFormat(timeTask.getTaskTime(),"dd");
                String time = DateUtils.dateFormat(timeTask.getTaskTime(),"HH:mm:ss");
                Date currentRunTime =  DateUtils.parse(DateUtils.dateFormat(new Date())+ " " +DateUtils.dateFormat(timeTask.getTaskTime(),"HH:mm:ss"),"yyyy-MM-dd HH:mm:ss");

                if(timeTask.getIntervalType()==1){
                    if(currentRunTime.getTime()>(new Date()).getTime()){
                        timeTask.setNextTask(currentRunTime);
                    }else{
                        //小时
                        Date nextTime = DateUtils.hourAddNum(timeTask.getTaskTime(),timeTask.getTaskDay());
                        nextTime = DateUtils.parse(DateUtils.dateFormat(new Date())+ " " +DateUtils.dateFormat(nextTime,"HH:mm:ss"),"yyyy-MM-dd HH:mm:ss");
                        timeTask.setNextTask(nextTime);
                    }
                }else if(timeTask.getIntervalType()==2){
                    if(currentRunTime.getTime()>(new Date()).getTime()){
                        timeTask.setNextTask(currentRunTime);
                    }else{
                        //天
                        timeTask.setNextTask(DateUtils.dayAddNum(timeTask.getTaskTime(),timeTask.getTaskDay()));
                    }
                }else if(timeTask.getIntervalType()==3){
                    int current_week = DateUtils.getWeekNum(new Date());
                    if(current_week<timeTask.getTaskDay()){
                        //本周执行
                        Date nextDay = DateUtils.dayAddNum(new Date(),(timeTask.getTaskDay()-current_week));
                        String nextTime = DateUtils.dateFormat(nextDay)+" "+time;
                        timeTask.setNextTask(DateUtils.parse(nextTime,"yyyy-MM-dd HH:mm:ss"));
                    }else{
                        if(current_week==timeTask.getTaskDay() && currentRunTime.getTime()>(new Date()).getTime()){
                            timeTask.setNextTask(currentRunTime);
                        }else{
                            //下周执行
                            timeTask.setNextTask(DateUtils.dayAddNum(new Date(),7));
                        }
                    }
                }else if(timeTask.getIntervalType()==4){
                    if(day.equals(DateUtils.dateFormat(timeTask.getTaskTime(),"dd"))&&currentRunTime.getTime()>(new Date()).getTime()){
                        timeTask.setNextTask(currentRunTime);
                    }else{
                        //月
                        int days = DateUtils.getDays(Integer.parseInt(year),Integer.parseInt(month));
                        Date next_month = DateUtils.dayAddNum(timeTask.getTaskTime(),days);
                        timeTask.setNextTask(DateUtils.parse(DateUtils.dateFormat(next_month,"yyyy-MM-")+timeTask.getTaskDay() + " " + time,"yyyy-MM-dd HH:mm:ss"));                }
                    }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //添加任务事件通知
    private void addEvent(TimeTask timeTask){
        Notification notification = new Notification();
        //写入设备ID
        notification.setDevId(timeTask.getTransformId());
        //写入设备类型,默认为台区
        notification.setDevType(2);
        //写入定时任务ID
        notification.setEventValue(String.valueOf(timeTask.getId()));
        //事件类型，计划任务
        notification.setEventType(100);
        //事件状态设置为初始状态为未处理
        notification.setEventStatus(0);
        //设置事件接收端为前置机
        notification.setEventReceiver(2);
        //设置优先级
          notification.setEventPriority(10);
        notificationRepository.save(notification);
    }
}