package com.wt.mis.fi.controller;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.fi.entity.DaySms;
import com.wt.mis.fi.repository.DaySmsRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
public class DaySmsShowController {

    @Autowired
    DaySmsRepository daySmsRepository;

    @Autowired
    SearchService searchService;

    @GetMapping("/sms/{day}/{id}")
    public ModelAndView show(@PathVariable String day,@PathVariable long id) {
        ModelAndView mv = new ModelAndView("fi/daysms/show");
        DaySms daysms = daySmsRepository.getOne(id);
        Date sendTime = DateUtils.parse(day + " " + daysms.getSendTime(),"yyyy-MM-dd HH:mm:ss");
        Date beginTime = DateUtils.minuteAddNum(sendTime,-15);
        Date endTime = DateUtils.minuteAddNum(sendTime,15);

        //将获取到的历史测量信息按照设备进行分组显示
        Map devMap = new HashMap<String ,List<HubVal>>();
        //获取遥测历史数据
        String sql = "SELECT t1.*,t2.hub_location,t2.line_id,t3.line_name FROM fi_yc_history t1 left join fi_dev_hub t2 on t1.hub_id = t2.id left join fi_line t3 on t2.line_id = t3.id" +
                "where t1.hub_id in ("+daysms.getDevIds()+") and t1.infor_addr in ("+daysms.getPointTypes()+") and t1.del = 0 and t1.create_time between '"+DateUtils.dateTimeFormat(beginTime)+"' and '"+DateUtils.dateTimeFormat(endTime)+"' order by t2.id, t1.create_time asc";
        List ycList = searchService.findAllBySql(sql);
        this.dealSearchList(ycList,devMap,"yc");



        //获取遥信数据
        sql = "SELECT t1.*,t2.hub_location,t2.line_id,t3.line_name FROM fi_yx_history t1 left join fi_dev_hub t2 on t1.hub_id = t2.id left join fi_line t3 on t2.line_id = t3.id" +
                "where t1.hub_id in ("+daysms.getDevIds()+") and t1.infor_addr in ("+daysms.getPointTypes()+") and t1.del = 0 and t1.create_time between '"+DateUtils.dateTimeFormat(beginTime)+"' and '"+DateUtils.dateTimeFormat(endTime)+"' order by t2.id, t1.create_time asc";
        List yxList = searchService.findAllBySql(sql);
        this.dealSearchList(ycList,devMap,"yx");

        List<DaySmsShow> smsShowsList = new ArrayList<>();

        Set<String> keys = devMap.keySet();
        for(String keyName: keys){
            String[] keyNameArr = keyName.split("=");
            List<HubVal> list = (List<HubVal>) devMap.get(keyName);
            DaySmsShow daySmsShow = new DaySmsShow(keyNameArr[0],keyNameArr[1],list);
            smsShowsList.add(daySmsShow);
        }
        mv.addObject("smsShowsList",smsShowsList);
        return mv;
    }

    private void dealSearchList(List searchResultlist,Map<String,List<HubVal>> devMap,String type) {
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

            String keyName = String.valueOf(map.get("line_name"))+"="+String.valueOf(map.get("hub_location"))+"="+String.valueOf(map.get("infor_addr"));
            if(devMap.containsKey(keyName)){
                List<HubVal> list = devMap.get(keyName);
                list.add(new HubVal(String.valueOf(map.get("infor_addr")),String.valueOf(map.get(type+"_value"))));
                devMap.put(keyName,list);
            }else{
                List<HubVal> list = new ArrayList<>();
                list.add(new HubVal(String.valueOf(map.get("infor_addr")),String.valueOf(map.get(type+"_value"))));
                devMap.put(keyName,list);
            }
        }
    }

    @Data
    class DaySmsShow{
        DaySmsShow(String lineName,String hubName,List hubValList){
            this.lineName = lineName;
            this.hubName = hubName;
            this.hubValList = hubValList;
        }
        private String happenTime;
        private String lineName;
        private String hubName;
        private List<HubVal> hubValList;
    }

    @Data
    class HubVal{
        HubVal(String locationAddr,String val){
            this.locationAddr = locationAddr;
            this.val = val;
        }
        private String locationAddr;
        private String val;
    }
}
