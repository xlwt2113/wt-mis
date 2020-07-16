
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fuse.entity.FaultData;
import com.wt.mis.fuse.repository.FaultDataRepository;
import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.repository.DictRepository;
import com.wt.mis.sys.util.DictUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/fuse/faultdata")
public class FaultDataController extends BaseController<FaultData> {

    @Autowired
    FaultDataRepository faultDataRepository;

    @Autowired
    DictRepository dictRepository;

    @Autowired
    DictItemRepository dictItemRepository;

    @Override
    public BaseRepository<FaultData, Long> repository() {
        return faultDataRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/faultdata";
    }


    @RequestMapping("/event_types")
    @ResponseBody
    public List<Node> dictJson(){
        Dict dict = dictRepository.getFirstByDictNameAndDel("熔断器事件类型",0);
        List<DictItem> list = dictItemRepository.getAllByDictAndDel(dict,0);
        List nodeList = new ArrayList();
        for(DictItem item:list){
            if(!item.getItemValue().equals("105")&&!item.getItemValue().equals("305")&&!item.getItemValue().equals("205")){
                Node node = new Node(item.getItemValue(),item.getItemKey());
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    class Node{
        private String id;
        private String name;

        Node(String id ,String name){
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    @Override
    protected String generateSearchSql(FaultData faultData, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_location,t2.hub_address from fuse_fault_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t2.hub_location like '%" + request.getParameter("hubName") + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("eventTypes"))) {
            sql.append(" and t1.event_type in ("+ request.getParameter("eventTypes") + ")");
        }
        if(StringUtils.isNotEmpty(request.getParameter("beginTime"))){
            String beginTime = request.getParameter("beginTime");
            sql.append(" and t1.update_time >= '"+beginTime+"'");
        }
        if(StringUtils.isNotEmpty(request.getParameter("endTime"))){
            String endTime = request.getParameter("beginTime");
            sql.append(" and t1.update_time <= '"+endTime+"'");
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
            key = DictUtils.getDictItemKey("熔断器事件类型", String.valueOf(map.get("event_type")));
            map.put("event_type_name", key);
        }
    }
}

