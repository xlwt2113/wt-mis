
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dev/transform")
public class TransFormController extends BaseController<TransForm> {

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    TopologyRepository topologyRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    DevService devService;

    @Override
    public BaseRepository<TransForm, Long> repository() {
        return transFormRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/transform";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, TransForm transForm) {
        super.initModel(id, model, transForm);
        model.addAttribute("lines", lineRepository.getAllByDel(0));
    }

    @Override
    protected String generateSearchSql(TransForm transForm, HttpServletRequest request) {
        Dep dep = new Dep();
        if(transForm.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(transForm.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_transform as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(transForm.getTransformName())) {
            sql.append(" and t1.transform_name like '%" + transForm.getTransformName() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getTransformNum())) {
            sql.append(" and t1.transform_num like '%" + transForm.getTransformNum() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        TransForm dev = transFormRepository.getOne(id);
        mv.addObject("operationsTeam",depRespository.getOne(dev.getOperationsTeam()).getName());
        return mv;
    }

    @ApiOperation("打开台区汇总界面")
    @GetMapping("/view_detail")
    public ModelAndView openViewDetailPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = new ModelAndView(this.getUrlPrefix() + "/detail");
        TransForm dev = transFormRepository.getOne(id);
        //获取当前台区总设备数
        List allDevList = topologyRepository.findAllByDelAndTransformId(0,dev.getId());
        mv.addObject("allDevCnt",allDevList.size());
        //获取分支箱数量
        List branchBoxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,3,dev.getId());
        mv.addObject("branchBoxCnt",branchBoxList.size());
        //获取单相表箱数量
        List meterBoxDxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,4,dev.getId());
        mv.addObject("meterBoxDxCnt",meterBoxDxList.size());
        //获取三相表箱数量
        List meterBoxSxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,5,dev.getId());
        mv.addObject("meterBoxSxCnt",meterBoxSxList.size());
        //获取单相电能表数量
        List meterDxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,6,dev.getId());
        mv.addObject("meterDxCnt",meterDxList.size());
        //获取三相电能表数量
        List meterSxList = topologyRepository.findAllByDelAndDevTypeAndTransformId(0,7,dev.getId());
        mv.addObject("meterSxCnt",meterSxList.size());
        //获取设备在线数量
        List onLineList = topologyRepository.findAllByDelAndDevOnlineAndTransformId(0,0,dev.getId());
        mv.addObject("onLine",((onLineList.size()*100/allDevList.size()))+ "%" );
        //改台区报警设备数
        List list = searchService.findAllBySql("select count(*) as cnt from dev_topology where del = 0 and transform_id = "+dev.getId()+" and CONCAT_WS('-',dev_id,dev_type) in (" +
                "SELECT CONCAT_WS('-',dev_id,dev_type) FROM event_power_outage where del = 0 and history = 0 and power_status = 1 " +
                ")");
        mv.addObject("alarmCnt",((HashMap) list.get(0)).get("cnt"));

        mv.addObject("operationsTeam",depRespository.getOne(dev.getOperationsTeam()).getName());
        mv.setViewName(this.getUrlPrefix()+"/detail");

        return mv;
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,2);
            return ResponseUtils.okJson("删除成功",id);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),id);
        }
    }

    @Override
    @ApiOperation("提交删除对象-批量删除")
    @PostMapping("/delete")
    @ResponseBody
    protected String deleteIds(@RequestParam("ids") List<Long> ids) {
        try{
            devService.deleteDevs(ids,2);
            return ResponseUtils.okJson("删除成功",ids);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),ids);
        }
    }
}

