
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fuse.entity.RealData;
import com.wt.mis.fuse.repository.RealDataRepository;
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
@RequestMapping("/fuse/realdata")
public class RealDataController extends BaseController<RealData> {

    @Autowired
    RealDataRepository realDataRepository;

    @Override
    public BaseRepository<RealData, Long> repository() {
        return realDataRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/realdata";
    }

    @Override
    protected String generateSearchSql(RealData realDate, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_name from fuse_real_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t2.hub_name like '%" + request.getParameter("hubName") + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("eventType"))) {
            sql.append(" and t1.event_type = '"+ request.getParameter("eventType") + "'");
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
            map.replace("event_type", key);
        }
    }
}
