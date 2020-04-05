
package com.wt.mis.event.controller;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.data.entity.Freeze;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.event.repository.PowerOutageRepository;
import com.wt.mis.event.view.PowerOutageSumView;
import com.wt.mis.event.view.PowerOutageView;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/event/poweroutage")
public class PowerOutageController{

    @Autowired
    PowerOutageRepository powerOutageRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    DevService devService;

    protected String getUrlPrefix() {
        return "event/poweroutage";
    }

    @GetMapping("/list")
    protected ModelAndView listPage(Freeze freeze) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/list");
        freeze.setFreezeTime(new Date());
        mv.addObject("search", freeze);
        return mv;
    }

    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity list(HttpServletRequest request) {
        String devType = request.getParameter("devType");
        String devId = request.getParameter("devId");
        //电压类型
        String dyType = request.getParameter("dyType");
        String occurTimeBegin = request.getParameter("occurTimeBegin");
        String occurTimeEnd = request.getParameter("occurTimeEnd");
        String transFormId = request.getParameter("transFormId");
        if(StringUtils.isEmpty(transFormId)){
            Map<String,List> resultMap = new HashMap<>();
            resultMap.put("logList",null);
            resultMap.put("sumList",null);
            //设置map，避免前台页面js错误
            return ResponseUtils.ok("请选择台区后再查询数据！",resultMap);
        }else{
            StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from event_power_outage t1  ");
            sql.append(" LEFT JOIN dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
            sql.append(" LEFT JOIN dev_transform t3 on t3.id = t2.transform_id where t1.del = 0 ");
            sql.append(" and t2.transform_id = " + transFormId);
            if(StringUtils.isNotEmpty(devType)){
                sql.append(" and t1.dev_type = " + devType);
            }
            if(StringUtils.isNotEmpty(devId)){
                sql.append(" and t1.dev_id = " + devId);
            }
            if(StringUtils.isNotEmpty(occurTimeBegin)){
                sql.append(" and DATE_FORMAT(t1.occur_time,'%Y-%m-%d') >= '"+ occurTimeBegin +"'");
            }
            if(StringUtils.isNotEmpty(occurTimeEnd)){
                sql.append(" and DATE_FORMAT(t1.occur_time,'%Y-%m-%d') <= '"+ occurTimeEnd +"'");
            }
            if(StringUtils.isNotEmpty(dyType)){
                sql.append( " AND (voltage_status_a = "+dyType+" or voltage_status_b = "+dyType+" or voltage_status_c = "+dyType+")");
            }
            sql.append(" order by  t1.occur_time asc ");
            List list = searchService.findAllBySql(sql.toString());
            Map<String,List> resultMap = this.dealSearchList(list);
            return ResponseUtils.ok("获取到数据", resultMap);
        }
    }

    /**
     * 获取当前登录人管辖台区下的所有设备报警信息
     * @return
     */
    private List getCurrentPowerByDep(){
        StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from event_power_outage t1  ");
        sql.append(" LEFT JOIN dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
        sql.append(" LEFT JOIN dev_transform t3 on t3.id = t2.transform_id ");
        sql.append(" inner join sys_dep t4 on t3.operations_team = t4.id and (t4.level like '%_"+ LoginUser.getCurrentUser().getDepId() +"%' or t4.id = "+LoginUser.getCurrentUser().getDepId()+")");
        sql.append(" where t1.del = 0 and t1.power_status = 1 and t1.history = 0  and t2.del = 0");
        sql.append(" order by  t1.occur_time asc ");
        List list = searchService.findAllBySql(sql.toString());
        return list ;
    }

    @ApiOperation("根据当前正在停电的设备")
    @GetMapping("/current_power_list")
    @ResponseBody
    public ResponseEntity current_power_list() {
        Map<String,List> resultMap = this.dealSearchList(this.getCurrentPowerByDep());
        return ResponseUtils.ok("获取到数据", resultMap.get("logList"));
    }

    @ApiOperation("根据当前正在停电的设备数量")
    @GetMapping("/current_power_cnt")
    @ResponseBody
    public int current_power_cnt() {
        List list = this.getCurrentPowerByDep();
        int cnt  = list.size();
        return cnt;
    }


    @ApiOperation("获取个设备某天的停电报警记录")
    @PostMapping("/power_log_list/{devId}/{devType}/{occurTime}")
    @ResponseBody
    public ResponseEntity power_list(@PathVariable long devId,@PathVariable int devType,@PathVariable String  occurTime) {
        StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from event_power_outage t1  ");
        sql.append(" LEFT JOIN dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
        sql.append(" LEFT JOIN dev_transform t3 on t3.id = t2.transform_id where t1.del = 0 ");
        sql.append(" and t1.dev_id = " + devId);
        sql.append(" and t1.dev_type = " + devType);
        sql.append(" and DATE_FORMAT(t1.occur_time,'%Y-%m-%d') = '"+occurTime+"'");
        sql.append(" order by  t1.occur_time desc limit 50");
        List list = searchService.findAllBySql(sql.toString());
        Map<String,List> resultMap = this.dealSearchList(list);
        return ResponseUtils.ok("获取到数据", resultMap.get("logList"));
    }

    @ApiOperation("获取个设备最近50条的停电报警记录")
    @PostMapping("/power_log_list/{devId}/{devType}")
    @ResponseBody
    public ResponseEntity all_power_list(@PathVariable long devId,@PathVariable int devType) {
        StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from event_power_outage t1  ");
        sql.append(" LEFT JOIN dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
        sql.append(" LEFT JOIN dev_transform t3 on t3.id = t2.transform_id where t1.del = 0 ");
        sql.append(" and t1.dev_id = " + devId);
        sql.append(" and t1.dev_type = " + devType);
        sql.append(" order by  t1.occur_time desc limit 50");
        List list = searchService.findAllBySql(sql.toString());
        Map<String,List> resultMap = this.dealSearchList(list);
        return ResponseUtils.ok("获取到数据", resultMap.get("logList"));
    }


    /**
     * 处理列表页面中要显示的数据内容,将行转列显示
     *
     * @param searchResultlist
     */
    private Map<String,List> dealSearchList(List searchResultlist) {
        Map<String,List> resultMap = new HashMap<>();
        Map<String,PowerOutageSumView> sumMap = new HashMap<>();
        List result = new ArrayList();
        List resultSum = new ArrayList();
        //将字典项中的值替换成显示名称
        for (Object obj : searchResultlist) {
            HashMap<String, Object> map = (HashMap) obj;
            PowerOutageView view = new PowerOutageView();
            view.setDevId(Long.valueOf(map.get("dev_id").toString()));
            view.setOccurTime((Date) map.get("occur_time"));
            view.setOccurTimeStr(DateUtils.timeFormat(view.getOccurTime()));
            view.setDevTypeName(DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_type"))));
            view.setDevType(Integer.valueOf(map.get("dev_type").toString()));
            view.setDevParentType(DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_parent_type"))));
            view.setPowerStatus(DictUtils.getDictItemKey("停电/相序状态", String.valueOf(map.get("power_status"))));
            view.setPhaseStatus(DictUtils.getDictItemKey("停电/相序状态", String.valueOf(map.get("phase_status"))));
            view.setVoltageStatusA(DictUtils.getDictItemKey("电压状态", String.valueOf(map.get("voltage_status_a"))));
            view.setVoltageStatusB(DictUtils.getDictItemKey("电压状态", String.valueOf(map.get("voltage_status_b"))));
            view.setVoltageStatusC(DictUtils.getDictItemKey("电压状态", String.valueOf(map.get("voltage_status_c"))));
            view.setDevName(String.valueOf(map.get("dev_name")));
            view.setDevParentName(String.valueOf(map.get("dev_parent_name")));
            view.setTransformName(String.valueOf(map.get("transform_name")));
            result.add(view);

            PowerOutageSumView sumView = new PowerOutageSumView();
            if(sumMap.containsKey(String.valueOf(map.get("dev_id")))){
                sumView = (PowerOutageSumView)sumMap.get(String.valueOf(map.get("dev_id")));
                sumView.setCnt(sumView.getCnt()+1);
            }else{
                sumView.setDevName(view.getDevName());
                sumView.setDevType(view.getDevTypeName());
                sumView.setCnt(1);
            }
            sumMap.put(String.valueOf(map.get("dev_id")),sumView);
        }

        Iterator it = sumMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            resultSum.add(entry.getValue());
        }

        resultMap.put("logList",result);
        resultMap.put("sumList",resultSum);

        return resultMap;
    }

}