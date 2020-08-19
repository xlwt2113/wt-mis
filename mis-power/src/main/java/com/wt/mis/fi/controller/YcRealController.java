
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.YcReal;
import com.wt.mis.fi.repository.YcRealRepository;
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
@RequestMapping("/fi/ycreal")
public class YcRealController extends BaseController<YcReal> {

    @Autowired
    YcRealRepository ycRealRepository;

    @Override
    public BaseRepository<YcReal, Long> repository() {
        return ycRealRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/ycreal";
    }

    @Override
    protected String generateSearchSql(YcReal ycReal, HttpServletRequest request) {

        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_location,DATE_FORMAT(t1.update_time,'%Y-%m-%d %H:%i') as update_time_str from fi_yc_real as t1 left join fi_dev_hub t2 on t1.hub_id = t2.id  where t1.del = 0 ");
        sql.append(" and t1.hub_id = "+request.getParameter("hubId"));

        if(StringUtils.isNotEmpty(request.getParameter("beginTime"))){
            String beginTime = request.getParameter("beginTime");
            sql.append(" and t1.update_time >= '"+beginTime+"'");
        }
        if(StringUtils.isNotEmpty(request.getParameter("endTime"))){
            String endTime = request.getParameter("endTime");
            sql.append(" and t1.update_time <= '"+endTime+"'");
        }
        if(StringUtils.isNotEmpty(request.getParameter("inforAddr"))){
            sql.append(" and t1.infor_addr = '"+request.getParameter("inforAddr")+"'");
        }
        sql.append(" order by t1.id asc");
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
            String addr = Integer.toHexString(Integer.parseInt(String.valueOf(map.get("infor_addr")))).toUpperCase();
            for(int i=addr.length();i<4;i++){
                addr = "0" + addr;
            }
            map.replace("infor_addr",addr);
        }
    }

}