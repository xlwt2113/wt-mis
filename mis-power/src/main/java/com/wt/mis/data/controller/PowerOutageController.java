
package com.wt.mis.data.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.data.entity.PowerOutage;
import com.wt.mis.data.repository.PowerOutageRepository;
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
@RequestMapping("/data/poweroutage")
public class PowerOutageController extends BaseController<PowerOutage> {

    @Autowired
    PowerOutageRepository powerOutageRepository;

    @Override
    public BaseRepository<PowerOutage, Long> repository() {
        return powerOutageRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "data/poweroutage";
    }

    @Override
    protected String generateSearchSql(PowerOutage powerOutage, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.dev_name from event_power_outage t1 left join dev_topology t2 on t1.dev_id = t2.dev_id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(powerOutage.getDevId())) {
            sql.append(" and t2.dev_name like '%" + powerOutage.getDevId() + "%'");
        }
        if (StringUtils.isNotEmpty(powerOutage.getDevType())) {
            sql.append(" and t1.dev_type like '%" + powerOutage.getDevType() + "%'");
        }
        return sql.toString();
    }

    /**
    * 处理列表页面中要显示的数据内容
    * @param searchResultlist
    */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for(Object obj:searchResultlist){
            HashMap<String,String> map = (HashMap) obj;
            String key = "";
            key = DictUtils.getDictItemKey("设备类型",map.get("dev_type"));
            map.replace("dev_type",key);
            key = DictUtils.getDictItemKey("停电/相序状态",map.get("power_status"));
            map.replace("power_status",key);
            key = DictUtils.getDictItemKey("停电/相序状态",map.get("phase_status"));
            map.replace("phase_status",key);
            key = DictUtils.getDictItemKey("电压状态",map.get("voltage_statusa"));
            map.replace("voltage_status_a",key);
            key = DictUtils.getDictItemKey("电压状态",map.get("voltage_statusb"));
            map.replace("voltage_status_b",key);
            key = DictUtils.getDictItemKey("电压状态",map.get("voltage_statusc"));
            map.replace("voltage_status_c",key);
        }
    }
    }