
package com.wt.mis.data.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.data.entity.Freeze;
import com.wt.mis.data.repository.FreezeRepository;
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
@RequestMapping("/data/freeze")
public class FreezeController extends BaseController<Freeze> {

    @Autowired
    FreezeRepository freezeRepository;

    @Override
    public BaseRepository<Freeze, Long> repository() {
        return freezeRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "data/freeze";
    }

    @Override
    protected String generateSearchSql(Freeze freeze, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from data_freeze as t1  where t1.del = 0 ");
        if (freeze.getDevType() != null) {
            sql.append(" and t1.dev_type = " + freeze.getDevType());
        }
        if (freeze.getCallTime() != null) {
            sql.append(" and t1.call_time = " + freeze.getCallTime());
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
            key = DictUtils.getDictItemKey("设备类型", map.get("dev_type"));
            map.replace("dev_type", key);
            key = DictUtils.getDictItemKey("冻结数据类型", map.get("data_type"));
            map.replace("data_type", key);
        }
    }
}