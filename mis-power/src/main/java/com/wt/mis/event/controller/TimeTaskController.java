
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.event.entity.TimeTask;
import com.wt.mis.event.repository.TimeTaskRepository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}