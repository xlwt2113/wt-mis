
package com.wt.mis.dev.controller;

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
@RequestMapping("/dev/topology")
public class TopologyController {

    @Autowired
    TopologyRepository topologyRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    PowerOutageRepository powerOutageRepository;

    @Autowired
    SearchService searchService;

    private String getUrlPrefix() {
        return "dev/topology";
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

    @GetMapping("/line_view_data")
    @ResponseBody
    public Map<String,List<Topology>> getLineViewData(){
        Map<String,List<Topology>> resultMap = new HashMap();
        List<Line> lineList = lineRepository.findAllByOperationsTeam(LoginUser.getCurrentUser().getDepId());
        List<Topology> topologyLineList = new ArrayList<>();
        for(Line line :lineList){
            Topology topologyLine = new Topology();
            topologyLine.setDevId(line.getId());
            topologyLine.setDevName(line.getLineName());
            topologyLine.setDevType(1);
            List<Topology> newTransformList = new ArrayList<>();
            //获取该线路下的台区
            List<TransForm> transformList = transFormRepository.findAllByDelAndLineId(0,line.getId());
            //将台区对象转换成拓扑对象，方便前台展示
            for(TransForm transForm : transformList){
                List<Topology> tempList = topologyRepository.findAllByDelAndDevIdAndDevType(0,transForm.getId(),2);
                //判断台区是否在拓扑表
                if(tempList!=null&&tempList.size()>0){
                    newTransformList.add(tempList.get(0));
                }else{
                    Topology topology = new Topology();
                    topology.setDevId(transForm.getId());
                    topology.setDevType(2);
                    topology.setDevName(transForm.getTransformName());
//                    设备在拓扑表不存在，状态默认为不在线
                    topology.setDevOnline(1);
                    newTransformList.add(topology);
                }
            }

            topologyLine.setTransfromList(newTransformList);
            topologyLineList.add(topologyLine);
        }
        resultMap.put("lineList",topologyLineList);
        return resultMap;
    }


    /**
     * 打开台区层的查看页面
     * @return
     */
    @GetMapping("/view_transform/{id}")
    public ModelAndView view(@PathVariable Long id){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/view_transform");
        TransForm transForm = transFormRepository.getOne(id);
        List<Topology> topologyList = topologyRepository.findAllByDelAndTransformId(0,id);
        //是否显示拓扑设备和实际设备数量不一致的提示信息
        boolean showDevCntError = false;
        if(transForm.getDevNum()!=topologyList.size()){
            showDevCntError = true;
        }
        mv.addObject("showDevCntError",showDevCntError);
        mv.addObject("id",id);
        mv.addObject("name",transForm.getTransformName());
        return mv;
    }

    /**
     * 根据设备ID及类型查看设备的信息
     * @param devId
     * @param devType
     * @return
     */
    @GetMapping("/dev_info/{devId}/{devType}")
    public ModelAndView view(@PathVariable Long devId,@PathVariable int devType){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/dev_info");
        mv.addObject("devId",devId);
        mv.addObject("devType",devType);
        mv.addObject("currentTime", DateUtils.dateFormat(new Date()));
        mv.addObject("yestodayTime", DateUtils.dateFormat(DateUtils.dayAddNum(new Date(),-1)));
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
        List<Topology> transFormList = topologyRepository.findAllByDelAndDevIdAndDevType(0,id,2);
        List<Topology> topologyList = topologyRepository.findAllByDelAndTransformId(0,id);
        resultMap.put("topologyList",topologyList);
        if(transFormList!=null&&transFormList.size()>0){
            resultMap.put("transForm",transFormList.get(0));
        }else{
            resultMap.put("transForm",null);
        }
        return resultMap;
    }

    /**
     * 获取台区下面的拓扑信息
     * @return
     */
    @GetMapping("/all_transform")
    @ResponseBody
    public Map<String,Object> getAllTransform(){
        Map<String,Object> resultMap = new HashMap();
        List<Topology> transformList = topologyRepository.findAllByDelAndDevType(0,2);
        resultMap.put("topologyList",transformList);
        return resultMap;
    }

    /**
     * 打开拓扑维护界面
     * @return
     */
    @GetMapping("/edit")
    public ModelAndView manageView(){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/edit");
        return mv;
    }

    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list")
    @ResponseBody
    protected ResponseEntity list(HttpServletRequest request,
                                  Topology topology,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        StringBuffer sql  = new StringBuffer("select t1.*,t2.transform_name from transform_dev_topology t1 LEFT JOIN transform_dev_transform t2 on t2.id = t1.transform_id where t1.del = 0 and t2.del = 0 ");
        if(topology.getDevType()!=null){
            sql.append(" and t1.dev_type = " + topology.getDevType().toString());
        }
        if(StringUtils.isNotEmpty(topology.getDevName())){
            sql.append(" and t1.dev_name like '%"+topology.getDevName()+"%'");
        }
        PageResult page = searchService.findBySql(sql.toString(), pageNumber, pageSize);

        //处理查询列表中的数据显示
        for(Object obj:page.getContent()){
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

        return ResponseUtils.ok("", page);
    }

}
