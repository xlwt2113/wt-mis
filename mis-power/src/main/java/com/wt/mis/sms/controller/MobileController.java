
package com.wt.mis.sms.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.sms.entity.JsonTreeModel;
import com.wt.mis.sms.entity.Mobile;
import com.wt.mis.sms.repository.MobileRepository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/sms/mobile")
public class MobileController extends BaseController<Mobile> {

    @Autowired
    MobileRepository mobileRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    TopologyRepository topologyRepository;

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

    @ResponseBody
    @GetMapping("line_tree")
    public List lineTree(){
        List<Line> lineList = lineRepository.findAllByOperationsTeam(LoginUser.getCurrentUser().getDepId());
        List<JsonTreeModel> treeData = new ArrayList<>();
        List<Topology> transformList = topologyRepository.findAllByDelAndDevType(0,2);
        for(Line line :lineList){
            JsonTreeModel jsonLine = new JsonTreeModel();
            jsonLine.setId("line_"+line.getId());
            jsonLine.setTitle(line.getLineName());
            jsonLine.setType(1);
            List<JsonTreeModel> childrens = new ArrayList<>();
            for(Topology transform :transformList){
                TransForm temp = transFormRepository.getOne(transform.getDevId());
                if(temp!=null && temp.getLineId()!=null && temp.getLineId().longValue() == line.getId().longValue()){
                    JsonTreeModel children = new JsonTreeModel();
                    children.setTitle(temp.getTransformName());
                    children.setId(String.valueOf(temp.getId()));
                    children.setType(2);
                    childrens.add(children);
                }
            }
            jsonLine.setChildren(childrens);
            treeData.add(jsonLine);
        }
        return treeData;
    }
}
