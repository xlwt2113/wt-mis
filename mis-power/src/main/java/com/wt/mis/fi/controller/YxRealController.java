
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.YxReal;
import com.wt.mis.fi.repository.YxRealRepository;
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
@RequestMapping("/fi/yxreal")
public class YxRealController extends BaseController<YxReal> {

    @Autowired
    YxRealRepository yxRealRepository;

    @Override
    public BaseRepository<YxReal, Long> repository() {
        return yxRealRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/yxreal";
    }

    @Override
    protected String generateSearchSql(YxReal yxReal, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_location from fi_yx_real as t1 left join fi_dev_hub as t2 on t1.hub_id = t2.id  where t1.del = 0 ");
        sql.append(" and t1.hub_id = "+request.getParameter("hubId"));
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
            String key = DictUtils.getDictItemKey("故障指示器信息体地址",String.valueOf(map.get("infor_addr")));
            map.put("infor_addr_name",key);
        }
    }

}
