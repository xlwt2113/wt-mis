
package com.wt.mis.sms.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sms.entity.Mobile;
import com.wt.mis.sms.repository.MobileRepository;
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
@RequestMapping("/sms/mobile")
public class MobileController extends BaseController<Mobile> {

    @Autowired
    MobileRepository mobileRepository;

    @Override
    public BaseRepository<Mobile, Long> repository() {
        return mobileRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sms/mobile";
    }

    @Override
    protected String generateSearchSql(Mobile mobile, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from sms_mobile as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(mobile.getMobile())) {
            sql.append(" and t1.mobile like '%" + mobile.getMobile() + "%'");
        }
        if (StringUtils.isNotEmpty(mobile.getName())) {
            sql.append(" and t1.name like '%" + mobile.getName() + "%'");
        }
        if (StringUtils.isNotEmpty(mobile.getGroupName())) {
            sql.append(" and t1.group_name like '%" + mobile.getGroupName() + "%'");
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
            key = DictUtils.getDictItemKey("短信接收人员分组",map.get("group_name"));
            map.replace("group_name",key);
        }
    }
}
