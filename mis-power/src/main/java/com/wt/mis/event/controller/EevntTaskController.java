
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.event.entity.EeventTask;
import com.wt.mis.event.repository.EeventTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/event/task")
public class EevntTaskController extends BaseController<EeventTask> {

    @Autowired
    EeventTaskRepository eevntTaskRepository;

    @Override
    public BaseRepository<EeventTask, Long> repository() {
        return eevntTaskRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "event/task";
    }

    @Override
    protected String generateSearchSql(EeventTask eventTask, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from event_task as t1  where t1.del = 0  and t1.account_id = "+ LoginUser.getCurrentUser().getId());
        return sql.toString();
    }




}
