
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
        StringBuffer sql = new StringBuffer("select t1.* from fuse_fault_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(faultData.getHubId())) {
            sql.append(" and t1.hub_name like '%" + faultData.getHubId() + "%'");
        }
        if (StringUtils.isNotEmpty(faultData.getEventType())) {
            sql.append(" and t1.event_type = '" + faultData.getEventType() + "'");
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
            key = DictUtils.getDictItemKey("熔断器事件类型", map.get("event_type"));
            map.replace("event_type", key);
        }
    }
}

