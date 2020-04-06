
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.event.entity.Notification;
import com.wt.mis.event.entity.TimeTask;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.event.repository.TimeTaskRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
        StringBuffer sql = new StringBuffer("select t1.*,t2.transform_name from time_task as t1 left join dev_transform t2 on t1.transform_id = t2.id  where t1.del = 0  and dep_id = " + LoginUser.getCurrentUser().getDepId());
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
            map.replace("interval_type", key);
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
        this.addEvent(timeTask);
        calculationDay(timeTask);
        return super.add(request, timeTask);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    protected String edit(HttpServletRequest request, TimeTask timeTask) {
        this.addEvent(timeTask);
        calculationDay(timeTask);
        return super.edit(request, timeTask);
    }

    //计算下一次的任务执行时间
    private void calculationDay(TimeTask timeTask){
        if(timeTask.getTaskTime() != null){
            if(timeTask.getIntervalType()==1){
                //小时
                timeTask.setNextTask(DateUtils.hourAddNum(timeTask.getTaskTime(),timeTask.getTaskDay()));
            }else if(timeTask.getIntervalType()==2){
                //天
                timeTask.setNextTask(DateUtils.dayAddNum(timeTask.getTaskTime(),timeTask.getTaskDay()));
            }else if(timeTask.getIntervalType()==3){
                int week = DateUtils.getWeekNum(timeTask.getTaskTime());
                if(week>timeTask.getTaskDay()){
                    //本周执行
                    timeTask.setNextTask(DateUtils.dayAddNum(timeTask.getTaskTime(),(week-timeTask.getTaskDay())));
                }else{
                    //下周执行
                    timeTask.setNextTask(DateUtils.dayAddNum(timeTask.getTaskTime(),7));
                }


            }else if(timeTask.getIntervalType()==4){
                //月
                String year = DateUtils.dateFormat(timeTask.getTaskTime(),"yyyy");
                String day = DateUtils.dateFormat(timeTask.getTaskTime(),"dd");
                String month = DateUtils.dateFormat(timeTask.getTaskTime(),"MM");
                String time = DateUtils.dateFormat(timeTask.getTaskTime(),"HH:mm:ss");
                int days = DateUtils.getDays(Integer.parseInt(year),Integer.parseInt(month));
                month = DateUtils.dateFormat(DateUtils.dayAddNum(timeTask.getTaskTime(),days));
                days = DateUtils.getDays(Integer.parseInt(year),Integer.parseInt(month));
                if(days<Integer.parseInt(day)){
                    day = String.valueOf(days);
                }
                try{
                    timeTask.setNextTask(DateUtils.parse(year+"-"+month + "-"+day+" "+time,"yyyy-MM-dd HH:mm:ss"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //添加任务事件通知
    private void addEvent(TimeTask timeTask){
        Notification notification = new Notification();
        //写入设备ID
        notification.setDevId(timeTask.getTransformId());
        //写入设备类型,默认为台区
        notification.setDevType(2);
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