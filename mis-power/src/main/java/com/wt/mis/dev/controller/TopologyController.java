
package com.wt.mis.dev.controller;

import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Line> lineList = lineRepository.findAllByDel(0);
        List<Topology> topologyLineList = new ArrayList<>();
        for(Line line :lineList){
            Topology topologyLine = new Topology();
            topologyLine.setDevId(line.getId());
            topologyLine.setDevName(line.getLineName());
            topologyLine.setDevType(1);
            topologyLineList.add(topologyLine);
        }
        List transformList = topologyRepository.findAllByDelAndDevType(0,2);
        resultMap.put("transformList",transformList);
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

    @GetMapping("/transform_view_data/{id}")
    @ResponseBody
    public List<Topology> getTransformViewData(@PathVariable Long id){
        List<Topology> topologyList = topologyRepository.findAllByDelAndTransformId(0,id);

        return topologyList;
    }


}
