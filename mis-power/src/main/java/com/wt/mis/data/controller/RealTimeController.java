
package com.wt.mis.data.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.data.entity.RealTime;
import com.wt.mis.data.repository.RealTimeRepository;
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
@RequestMapping("/data/realtime")
public class RealTimeController extends BaseController<RealTime> {

    @Autowired
    RealTimeRepository realTimeRepository;

    @Override
    public BaseRepository<RealTime, Long> repository() {
        return realTimeRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "data/realtime";
    }

    @Override
    protected String generateSearchSql(RealTime realTime, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from data_real_time as t1  where t1.del = 0 ");
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
            key = DictUtils.getDictItemKey("数据类型", String.valueOf(map.get("data_type")));
            map.replace("data_type", key);
        }
    }
}