
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.dev.entity.TopologyUnnormal;
import com.wt.mis.dev.repository.TopologyUnnormalRepository;
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
@RequestMapping("/dev/topologyunnormal")
public class TopologyUnnormalController extends BaseController<TopologyUnnormal> {

    @Autowired
    TopologyUnnormalRepository topologyUnnormalRepository;

    @Override
    public BaseRepository<TopologyUnnormal, Long> repository() {
        return topologyUnnormalRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/topologyunnormal";
    }

    @Override
    protected String generateSearchSql(TopologyUnnormal topologyUnnormal, HttpServletRequest request) {
        StringBuffer sql  = new StringBuffer("select t1.*,t2.transform_name from dev_topology_unnormal t1 " +
                " LEFT JOIN dev_transform t2 on t2.id = t1.transform_id where t1.del = 0 and t2.del = 0 " +
                " and t1.transform_id = " + request.getParameter("transformId"));
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
            String key = DictUtils.getDictItemKey("设备类型",String.valueOf(map.get("dev_type")));
            map.replace("dev_type_name",key);

            if("0".equals(String.valueOf(map.get("dev_online")))){
                map.replace("dev_online","在线");
            }else{
                map.replace("dev_online","<font color='red'>离线</font>");
            }

            if("0".equals(String.valueOf(map.get("dev_exist")))){
                map.replace("dev_exist","存在");
            }else{
                map.replace("dev_exist","<font color='red'>不存在</font>");
            }
        }

    }


}
