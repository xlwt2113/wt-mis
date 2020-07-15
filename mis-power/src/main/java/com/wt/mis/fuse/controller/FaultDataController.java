
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fuse.entity.FaultData;
import com.wt.mis.fuse.repository.FaultDataRepository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/fuse/faultdata")
public class FaultDataController extends BaseController<FaultData> {

    @Autowired
    FaultDataRepository faultDataRepository;

    @Override
    public BaseRepository<FaultData, Long> repository() {
        return faultDataRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/faultdata";
    }

    @Override
    protected String generateSearchSql(FaultData faultData, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_name from fuse_fault_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t2.hub_name like '%" + request.getParameter("hubName") + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("eventType"))) {
            sql.append(" and t1.event_type = '" + request.getParameter("eventType") + "'");
        }
        if(StringUtils.isNotEmpty(request.getParameter("beginTime"))){
            String beginTime = request.getParameter("beginTime") + " 00:00:00";
            sql.append(" and t1.update_time >= '"+beginTime+"'");
        }
        if(StringUtils.isNotEmpty(request.getParameter("endTime"))){
            String endTime = request.getParameter("beginTime") + " 23:59:59";
            sql.append(" and t1.update_time <= '"+endTime+"'");
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
            key = DictUtils.getDictItemKey("熔断器事件类型", String.valueOf(map.get("event_type")));
            map.put("event_type_name", key);
        }
    }
}

