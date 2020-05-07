
package com.wt.mis.data.controller;

import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.data.view.RealTimeView;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.DevModel;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/data/realtime")
public class RealTimeController{

    @Autowired
    SearchService searchService;

    @Autowired
    DevService devService;


    protected String getUrlPrefix() {
        return "data/realtime";
    }


    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity list(HttpServletRequest request) {
        String devType = request.getParameter("devType");
        String devId = request.getParameter("devId");
        String callTime = request.getParameter("callTime");
        if(StringUtils.isEmpty(devId)||StringUtils.isEmpty(devType)||StringUtils.isEmpty(callTime)){
            return ResponseUtils.ok("没有参数，不查询数据",null);
        }else{
            StringBuffer sql = new StringBuffer(" SELECT t1.*,t2.dev_name,t2.dev_parent_type,t2.dev_parent_name,  t3.transform_name,t2.transform_id from transform_data_real_time t1  ");
            sql.append(" LEFT JOIN transform_dev_topology t2 on t1.dev_id = t2.dev_id and t1.dev_type = t2.dev_type ");
            sql.append(" LEFT JOIN transform_dev_transform t3 on t3.id = t2.transform_id where t1.del = 0");
            sql.append(" and t1.dev_type = "+devType+" and t1.dev_id = " + devId);
            sql.append(" and DATE_FORMAT(t1.call_time,'%Y-%m-%d') = '"+ callTime +"'");
            sql.append(" order by  t1.call_time asc ");
            List list = searchService.findAllBySql(sql.toString());
            DevModel dev = devService.getDevModel(Long.parseLong(devId),Integer.parseInt(devType));
            list = this.dealSearchList(list,dev);
            return ResponseUtils.ok("获取到数据", list);
        }

    }


    /**
     * 处理列表页面中要显示的数据内容,将行转列显示
     *
     * @param searchResultlist
     */
    private List dealSearchList(List searchResultlist, DevModel devModel) {
        List result = new ArrayList();
        HashMap<String, RealTimeView> rowMap = new HashMap();
        //将字典项中的值替换成显示名称
        for (Object obj : searchResultlist) {
            HashMap<String, Object> map = (HashMap) obj;
            RealTimeView view = new RealTimeView();
            String freezeTimeStr = DateUtils.dateTimeFormat((Date)map.get("call_time"));
            view.setCallTime((Date)map.get("call_time"));
            view.setCallTimeStr(DateUtils.timeFormat(view.getCallTime()));
            if(map.get("call_data_1")!=null){
                view.setRealDyDataA(Double.parseDouble(String.valueOf(map.get("call_data_1"))));
            }
            if(map.get("call_data_2")!=null){
                view.setRealDyDataB(Double.parseDouble(String.valueOf(map.get("call_data_2"))));
            }
            if(map.get("call_data_3")!=null){
                view.setRealDyDataC(Double.parseDouble(String.valueOf(map.get("call_data_3"))));
            }
            if(map.get("call_data_4")!=null){
                view.setRealDlDataA(Double.parseDouble(String.valueOf(map.get("call_data_4"))));
            }
            if(map.get("call_data_5")!=null){
                view.setRealDlDataB(Double.parseDouble(String.valueOf(map.get("call_data_5"))));
            }
            if(map.get("call_data_6")!=null){
                view.setRealDlDataC(Double.parseDouble(String.valueOf(map.get("call_data_6"))));
            }
            view.setCallTime((Date)map.get("call_time"));
            view.setDevType(DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_type"))));
            view.setDevParentType(DictUtils.getDictItemKey("设备类型", String.valueOf(map.get("dev_parent_type"))));
            view.setDevName(map.get("dev_name").toString());
            view.setDevParentName(map.get("dev_parent_name").toString());
            view.setTransformName(map.get("transform_name").toString());
            view.setOperationsTeamName(devModel.getOperationsTeamName());
            rowMap.put(freezeTimeStr,view);
        }

        Iterator it = rowMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            result.add(entry.getValue());
        }

        Collections.sort(result, new Comparator<RealTimeView>() {
            @Override
            public int compare(RealTimeView o1, RealTimeView o2) {
                if(o1.getCallTime().getTime()>o2.getCallTime().getTime()){
                    return 1;
                }else if(o1.getCallTime().getTime()<o2.getCallTime().getTime()){
                    return -1;
                }else{
                    return 0;
                }
            }
        });
        return result;
    }
}