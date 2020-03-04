
package com.wt.mis.event.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.event.entity.PowerOutage;
import com.wt.mis.event.repository.PowerOutageRepository;
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
@RequestMapping("/event/poweroutage")
public class PowerOutageController extends BaseController<PowerOutage> {

    @Autowired
    PowerOutageRepository powerOutageRepository;

    @Override
    public BaseRepository<PowerOutage, Long> repository() {
        return powerOutageRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "event/poweroutage";
    }

    @Override
    protected String generateSearchSql(PowerOutage powerOutage, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from event_power_outage as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(powerOutage.getDevId())) {
            sql.append(" and t1.dev_id like '%" + powerOutage.getDevId() + "%'");
        }
        if (StringUtils.isNotEmpty(powerOutage.getDevType())) {
            sql.append(" and t1.dev_type like '%" + powerOutage.getDevType() + "%'");
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
            key = DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_type")));
            map.replace("dev_type", key);
            key = DictUtils.getDictItemKey("停电状态", String.valueOf(map.get("power_status")));
            map.replace("power_status", key);
            key = DictUtils.getDictItemKey("相序状态", String.valueOf(map.get("phase_status")));
            map.replace("phase_status", key);
            key = DictUtils.getDictItemKey("A相电压状态", String.valueOf(map.get("voltage_status_a")));
            map.replace("voltage_status_a", key);
            key = DictUtils.getDictItemKey("B相电压状态", String.valueOf(map.get("voltage_status_b")));
            map.replace("voltage_status_b", key);
            key = DictUtils.getDictItemKey("C相电压状态", String.valueOf(map.get("voltage_status_c")));
            map.replace("voltage_status_c", key);
        }
    }
}