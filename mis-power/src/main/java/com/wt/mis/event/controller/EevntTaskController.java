
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.event.entity.EventTask;
import com.wt.mis.event.repository.EeventTaskRepository;
import com.wt.mis.event.repository.NotificationRepository;
import com.wt.mis.sys.repository.AccountRepository;
import com.wt.mis.sys.repository.DepRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/event/task")
public class EevntTaskController extends BaseController<EventTask> {

    @Autowired
    EeventTaskRepository evntTaskRepository;
    @Autowired
    SearchService searchService;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    DevService devService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<EventTask, Long> repository() {
        return evntTaskRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "event/task";
    }

    @Override
    protected String generateSearchSql(EventTask eventTask, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from transform_event_task as t1  where t1.del = 0  and t1.account_id = "+ LoginUser.getCurrentUser().getId());
        return sql.toString();
    }

    @Override
    protected ResponseEntity list(HttpServletRequest request, EventTask eventTask, Integer pageNumber, Integer pageSize, String pageSort, String pageDirection) {
        List<EventTask> list = evntTaskRepository.findAllByDelAndAccountId(0,LoginUser.getCurrentUser().getId());
        int cnt = getCurrentPowerCntByDep();
        if(cnt>0){
            EventTask alarm = new EventTask();
            alarm.setMsg("目前有"+cnt+"个设备报警！");
            list.add(alarm);
        }
        return ResponseUtils.ok("", list);
    }


    /**
     * 获取当前登录人管辖台区下的所有设备报警信息
     * @return
     */
    private int  getCurrentPowerCntByDep(){
        StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from transform_event_power_outage t1  ");
        sql.append(" LEFT JOIN transform_dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
        sql.append(" LEFT JOIN transform_dev_transform t3 on t3.id = t2.transform_id ");
        sql.append(" inner join sys_dep t4 on t3.operations_team = t4.id and (t4.level like '%_"+ LoginUser.getCurrentUser().getDepId() +"%' or t4.id = "+LoginUser.getCurrentUser().getDepId()+")");
        sql.append(" where t1.del = 0 and t1.power_status = 1 and t1.history = 0  and t2.del = 0");
        sql.append(" order by  t1.occur_time desc ");
        List list = searchService.findAllBySql(sql.toString());
        return list.size() ;
    }

}
