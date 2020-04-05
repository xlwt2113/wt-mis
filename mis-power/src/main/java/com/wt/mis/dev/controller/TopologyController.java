
package com.wt.mis.dev.controller;

import com.wt.mis.core.util.DateUtils;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.event.repository.PowerOutageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
//        List<Line> lineList = lineRepository.findAllByDel(0);
        List<Line> lineList = lineRepository.findAllByOperationsTeam(LoginUser.getCurrentUser().getDepId());
        List<Topology> topologyLineList = new ArrayList<>();
        List<Topology> transformList = topologyRepository.findAllByDelAndDevType(0,2);
        for(Line line :lineList){
            Topology topologyLine = new Topology();
            topologyLine.setDevId(line.getId());
            topologyLine.setDevName(line.getLineName());
            topologyLine.setDevType(1);
            List<Topology> newTransformList = new ArrayList<>();
            for(Topology transform :transformList){
                TransForm temp = transFormRepository.getOne(transform.getDevId());
                if(temp!=null && temp.getLineId()!=null && temp.getLineId().longValue() == line.getId().longValue()){
                    newTransformList.add(transform);
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
        mv.addObject("id",id);
        mv.addObject("name",transForm.getTransformName());
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
        resultMap.put("transForm",transFormList.get(0));
        return resultMap;
    }



    @GetMapping("/dev_info/{devId}/{devType}")
    public ModelAndView view(@PathVariable Long devId,@PathVariable int devType){
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/dev_info");
        mv.addObject("devId",devId);
        mv.addObject("devType",devType);
        mv.addObject("currentTime", DateUtils.dateFormat(DateUtils.dayAddNum(new Date(),-1)));
        return mv;
    }

}
