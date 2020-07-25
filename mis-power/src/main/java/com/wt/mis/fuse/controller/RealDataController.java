
package com.wt.mis.fuse.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fuse.entity.DevHub;
import com.wt.mis.fuse.entity.EventNotification;
import com.wt.mis.fuse.entity.HistoryData;
import com.wt.mis.fuse.entity.RealData;
import com.wt.mis.fuse.repository.DevHubRepository;
import com.wt.mis.fuse.repository.EventNotificationRepository;
import com.wt.mis.fuse.repository.RealDataRepository;
import com.wt.mis.sys.controller.ApiSysController;
import com.wt.mis.sys.entity.Dict;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.repository.DictRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/fuse/realdata")
public class RealDataController extends BaseController<RealData> {

    @Autowired
    RealDataRepository realDataRepository;

    @Autowired
    DictRepository dictRepository;

    @Autowired
    DictItemRepository dictItemRepository;

    @Autowired
    DevHubRepository devHubRepository;

    @Autowired
    EventNotificationRepository eventNotificationRepository;

    /**
     * 用于获取列表数据的查询service
     */
    @Autowired
    SearchService searchService;

    @Override
    public BaseRepository<RealData, Long> repository() {
        return realDataRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fuse/realdata";
    }

    @RequestMapping("/event_types")
    @ResponseBody
    public List<Node> dictJson() {
        Dict dict = dictRepository.getFirstByDictNameAndDel("熔断器数据类型", 0);
        List<DictItem> list = dictItemRepository.getAllByDictAndDel(dict, 0);
        List nodeList = new ArrayList();
        for (DictItem item : list) {
//            if(item.getItemValue().equals("105")||item.getItemValue().equals("305")||item.getItemValue().equals("205")){
//                Node node = new Node(item.getItemValue(),item.getItemKey());
//                nodeList.add(node);
//            }
            Node node = new Node(item.getItemValue(), item.getItemKey());
            nodeList.add(node);
        }
        return nodeList;
    }

    class Node {
        private String id;
        private String name;

        Node(String id, String name) {
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
    protected String generateSearchSql(RealData realDate, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_location,t2.hub_address from fuse_real_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(request.getParameter("hubName"))) {
            sql.append(" and t2.hub_location like '%" + request.getParameter("hubName") + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("eventTypes"))) {
            sql.append(" and t1.data_type in (" + request.getParameter("eventTypes") + ")");
        }
        if (StringUtils.isNotEmpty(request.getParameter("beginTime"))) {
            String beginTime = request.getParameter("beginTime");
            sql.append(" and t1.update_time >= '" + beginTime + "'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("endTime"))) {
            String endTime = request.getParameter("endTime");
            sql.append(" and t1.update_time <= '" + endTime + "'");
        }
        sql.append(" order by t1.hub_id ,t1.data_type asc");
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
            map.replace("event_type", key);

            key = DictUtils.getDictItemKey("熔断器数据类型", String.valueOf(map.get("data_type")));
            map.put("data_type_name", key);
        }
    }

    @ApiOperation("召测数据")
    @GetMapping("/call_data/{hubId}/{dataType}")
    @ResponseBody
    public String callData(@PathVariable("hubId") Long hubId,@PathVariable("dataType") Integer dataType) {
        DevHub devHub = devHubRepository.findById(hubId).get();
        int eventType = 0;
        switch (dataType){
            case 1:eventType = 12;break;
            case 2:eventType = 22;break;
            case 3:eventType = 32;break;
        }
        if(eventType==0){
            return ResponseUtils.errorJson("数据类型参数错误！",hubId);
        }
        if(devHub.getOnlineStatus()==1){
            return ResponseUtils.errorJson("设备不在线，无法执行操作！",hubId);
        }
        List<EventNotification> list = eventNotificationRepository.findRuningEvent(hubId ,eventType);
        if(list!=null&&list.size()>0){
            return ResponseUtils.errorJson("任务正在执行中，请稍后在进行操作！",hubId);
        }
        EventNotification eventNotification = new EventNotification();
        eventNotification.setDel(0);
        eventNotification.setHubId(hubId);
        eventNotification.setEventType(eventType);
        eventNotification.setEventStatus(0);
        eventNotification.setEventReceiver(2);
        eventNotificationRepository.save(eventNotification);
        return ResponseUtils.okJson("召测命令发送成功",hubId);
    }


    //获取历史输入的sql
    private String generateHistorySearchSql(HttpServletRequest request) {
        String beginTime = DateUtils.dateFormat(new Date())+" 00:00:00";
        String endTime = DateUtils.dateFormat(new Date())+" 23:59:59";

        if (StringUtils.isNotEmpty(request.getParameter("beginTime"))) {
            beginTime = request.getParameter("beginTime");
        }
        if (StringUtils.isNotEmpty(request.getParameter("endTime"))) {
            endTime = request.getParameter("endTime");
        }

        StringBuffer sql = new StringBuffer("select t1.*,t2.hub_location,t2.hub_address from fuse_history_data as t1 left join fuse_dev_hub as t2 on t1.hub_id = t2.id where t1.del = 0 ");
        sql.append(" and t1.hub_id = "+ request.getParameter("hubId"));
        sql.append(" and t1.data_type = " + request.getParameter("dataType") );
        sql.append(" and t1.update_time >= '" + beginTime + "'");
        sql.append(" and t1.update_time <= '" + endTime + "'");
        sql.append(" order by t1.update_time  asc");
        return sql.toString();
    }


    /**
     * 获取历史数据
     * @param request
     * @return
     */
    @PostMapping("/history_list")
    @ResponseBody
    public ResponseEntity historyList(HttpServletRequest request) {
        List list = searchService.findAllBySql(this.generateHistorySearchSql(request));
        this.dealSearchList(list);
        return ResponseUtils.ok("", list);
    }

    /**
     * 打开历史数据页面
     * @return
     */
    @GetMapping("/history_list")
    protected ModelAndView historyListPage(HistoryData historyData) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/history_list");
        mv.addObject("search", historyData);
        String beginTime = DateUtils.dateFormat(new Date())+" 00:00:00";
        String endTime = DateUtils.dateFormat(new Date())+" 23:59:59";
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
        return mv;
    }

}
