
package com.wt.mis.fi.controller;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.event.repository.PowerOutageRepository;
import com.wt.mis.fi.entity.FiDevHub;
import com.wt.mis.fi.entity.FiLine;
import com.wt.mis.fi.entity.YcReal;
import com.wt.mis.fi.entity.YxReal;
import com.wt.mis.fi.repository.FiDevHubRepository;
import com.wt.mis.fi.repository.FiLineRepository;
import com.wt.mis.fi.repository.YxRealRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/fi/topology")
public class FiTopologyController {

    @Autowired
    FiLineRepository fiLineRepository;

    @Autowired
    FiDevHubRepository fiDevHubRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    YxRealRepository yxRealRepository;

    private String getUrlPrefix() {
        return "fi/topology";
    }

    /**
     * 打开户变关系展示页面
     * @return
     */
    @GetMapping("/view")
    public ModelAndView view(){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/view");
        return mv;
    }


    /**
     * 打开户变关系维护界面
     * @return
     */
    @GetMapping("/edit")
    public ModelAndView edit(){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/edit");
        return mv;
    }


    /**
     * 打开台区层的查看页面
     * @return
     */
    @GetMapping("/view_transform/{id}")
    public ModelAndView view(@PathVariable Long id){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/view_transform");
        FiDevHub fiDevHub = fiDevHubRepository.getOne(id);
        FiLine line = fiLineRepository.getOne(fiDevHub.getLineId());
        mv.addObject("id",id);
        mv.addObject("name",fiDevHub.getHubLocation());
        mv.addObject("line",line);
        return mv;
    }

    /**
     * 打开台区层的管理界面
     * @return
     */
    @GetMapping("/edit_transform/{id}")
    public ModelAndView edit(@PathVariable Long id){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/edit_transform");
        FiDevHub fiDevHub = fiDevHubRepository.getOne(id);
        mv.addObject("id",id);
        mv.addObject("name",fiDevHub.getHubLocation());
        return mv;
    }


    @GetMapping("/line_view_data")
    @ResponseBody
    public Map<String,List<FiLine>> getLineViewData(){
        Map<String,List<FiLine>> resultMap = new HashMap();
        List<FiLine> lineList = fiLineRepository.findAllByDel(0);
        for(FiLine line :lineList){
            //获取该线路下设备
            List<FiDevHub> devHubList = fiDevHubRepository.findAllByLineIdAndParentIdAndDel(line.getId(),0,0);
            line.setDevHubList(devHubList);
        }
        resultMap.put("lineList",lineList);
        return resultMap;
    }

    @PostMapping("/save_position")
    @ResponseBody
    public ResponseEntity savePositon(HttpServletRequest request){
        FiDevHub hub = fiDevHubRepository.getOne(Long.parseLong(request.getParameter("id")));
        hub.setPosX(Float.parseFloat(request.getParameter("posX")));
        hub.setPosY(Float.parseFloat(request.getParameter("posY")));
        this.fiDevHubRepository.save(hub);
        return ResponseUtils.ok("", hub);
    }

    /**
     * 根据设备ID及类型查看设备的信息
     * @param devId
     * @return
     */
    @GetMapping("/dev_info/{devId}")
    public ModelAndView devInfoView(@PathVariable Long devId){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/dev_info");
        FiDevHub fiDevHub = fiDevHubRepository.getOne(devId);
        mv.addObject("beginTime",DateUtils.dateFormat(new Date())+" 00:00:00");
        mv.addObject("endTime",DateUtils.dateFormat(new Date())+" 23:59:59");
        mv.addObject("hub",fiDevHub);
        mv.addObject("devId",devId);
        return mv;
    }

    /**
     * 获取台区下面的拓扑信息
     * @param id
     * @return
     */
    @GetMapping("/transform_view_data/{id}")
    @ResponseBody
    public Map<String,Object> getTransformViewData(@PathVariable Long id){
        Map<String,Object> resultMap = new HashMap();
        FiDevHub fiDevHub = fiDevHubRepository.getOne(id);
        List topologyList = new ArrayList();
        List<FiDevHub> fiDevHubList = fiDevHubRepository.findAllByLineIdAndDel(fiDevHub.getLineId(),0);
        //将线路上不属于所选节点下的子设备全部移除
        for(FiDevHub child:fiDevHubList){
            if(child.getParentId().longValue() == fiDevHub.getId().longValue()){
                topologyList.add(child);
                List childList = addChildNodes(child,fiDevHubList);
                topologyList.addAll(childList);
            }
        }

        resultMap.put("topologyList",topologyList);
        resultMap.put("transForm",fiDevHub);
        return resultMap;
    }

    //添加子节点
    private List addChildNodes(FiDevHub dev,List<FiDevHub> fiDevHubList){
        List topologyList = new ArrayList();
        for(FiDevHub child: fiDevHubList){
            if(child.getParentId().longValue() == dev.getId().longValue()){
                topologyList.add(child);
                List childList = addChildNodes(child,fiDevHubList);
                topologyList.addAll(childList);
            }
        }
        return topologyList;
    }

    @GetMapping("/chart")
    public ModelAndView openChartPage(@RequestParam("type") @NonNull int type,@RequestParam("hubId") @NonNull long hubId,@RequestParam("inforAddr") @NonNull String inforAddr,@RequestParam("beginTime") String beginTime,@RequestParam("endTime") String endTime){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/chart");
        mv.addObject("inforAddr", DictUtils.getDictItemKey("故障指示器信息体地址",inforAddr));
        return mv;
    }

    @GetMapping("/chart_data")
    @ResponseBody
    public Map<String ,List> chartJson(@RequestParam("type") @NonNull int type,@RequestParam("hubId") @NonNull long hubId,@RequestParam("inforAddr") @NonNull String inforAddr,@RequestParam("beginTime") String beginTime,@RequestParam("endTime") String endTime){
        Map result = new HashMap();
        String tableName = "";
        String colName ="";
        switch(type){
            case 1:
                tableName = "fi_yc_real";
                colName = "yc_value";
                break;
            case 2:
                colName = "yc_value";
                tableName = "fi_yc_history";break;
            case 3:
                colName = "yx_value";
                tableName = "fi_yx_real";break;
            case 4:
                colName = "yx_value";
                tableName = "fi_yx_history";break;
            default:
                break;
        }

        StringBuffer sql = new StringBuffer("select t1.*,DATE_FORMAT(t1.update_time,'%Y-%m-%d %H:%i:%s') as update_time_str,t2.hub_location from "+tableName+" as t1 left join fi_dev_hub as t2 on t1.hub_id = t2.id  where t1.del = 0 ");
        sql.append(" and t1.hub_id = "+hubId);

        if(StringUtils.isNotEmpty(beginTime)){
            sql.append(" and t1.update_time >= '"+beginTime+"'");
        }
        if(StringUtils.isNotEmpty(endTime)){
            sql.append(" and t1.update_time <= '"+endTime+"'");
        }
        if(StringUtils.isNotEmpty(inforAddr)){
            sql.append(" and t1.infor_addr = '"+inforAddr+"'");
        }
        sql.append(" order by update_time asc");

        List list = searchService.findAllBySql(sql.toString());
        Iterator it = list.iterator();
        List timeList = new ArrayList();
        List valList = new ArrayList();
        while(it.hasNext()){
            HashMap map = (HashMap) it.next();
            timeList.add(map.get("update_time_str"));
            valList.add(map.get(colName));
        }
        result.put("time",timeList);
        result.put("val",valList);
        return result;
    }


        /**
         * 获取报警设备信息
         * @return
         */
    @GetMapping("/current_alarm_list")
    @ResponseBody
    public List getAllTransform(){
        List list = yxRealRepository.findGroupByHubId();
        return list;
    }



}
