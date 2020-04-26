
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.MeterBox;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.repository.*;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.util.DictUtils;
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
@RequestMapping("/dev/meterbox")
public class MeterBoxController extends BaseController<MeterBox> {

    @Autowired
    BranchBoxRepository branchBoxRepository;
    @Autowired
    MeterBoxRepository meterBoxRepository;
    @Autowired
    MeterRepository meterRepository;
    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    DevService devService;

    @Autowired
    TopologyRepository topologyRepository;

    @Override
    public BaseRepository<MeterBox, Long> repository() {
        return meterBoxRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/meterbox";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, MeterBox meterbox) {
        super.initModel(id, model, meterbox);
        //初始化班组为当前登录人班组
        if(meterbox.getId()==null){
            meterbox.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
    }

    @Override
    protected String generateSearchSql(MeterBox meterBox, HttpServletRequest request) {
        Dep dep = new Dep();
        if(meterBox.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(meterBox.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_meter_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(meterBox.getMeterBoxName())) {
            sql.append(" and t1.meter_box_name like '%" + meterBox.getMeterBoxName() + "%'");
        }
        if (StringUtils.isNotEmpty(meterBox.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + meterBox.getInstallationLocation() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    /**
     * 处理列表页面中要显示的数据内容
     * @param searchResultlist
     */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for(Object obj:searchResultlist){
            HashMap<String,String> map = (HashMap) obj;
            String key = DictUtils.getDictItemKey("单/三相",String.valueOf(map.get("three_phase")));
            map.replace("three_phase",key);
        }
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        MeterBox meterBox = meterBoxRepository.getOne(id);
        mv.addObject("threePhase",DictUtils.getDictItemKey("单/三相",String.valueOf(meterBox.getThreePhase())));
        mv.addObject("operationsTeam",depRespository.getOne(meterBox.getOperationsTeam()).getName());
        return mv;
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, MeterBox meterBox) {
        int cnt = meterBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        cnt = cnt + this.getOtherDevCnt(meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",meterBox);
        }
        return super.add(request, meterBox);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    protected String edit(HttpServletRequest request, MeterBox meterBox) {
        int cnt = meterBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeamAndIdNot(0,meterBox.getProtocolAddress(),meterBox.getOperationsTeam(),meterBox.getId());
        cnt = cnt + this.getOtherDevCnt(meterBox.getProtocolAddress(),meterBox.getOperationsTeam());
        if(cnt>0){
            return ResponseUtils.errorJson("通讯地址已经存在！",meterBox);
        }
        List<Topology> topologyList = null;
        int[] devTypes = {4,5};
        if(meterBox.getThreePhase()==1){
            //单相
            topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,meterBox.getId(),4);
        }else{
            //三相
            topologyList = topologyRepository.findAllByDelAndDevIdAndDevType(0,meterBox.getId(),5);
        }
        topologyList = topologyRepository.findAllByDelAndDevIdAndDevTypeIn(0,meterBox.getId(),devTypes);
        for(Topology topology:topologyList){
            topology.setDevName(meterBox.getMeterBoxName());
        }
        return super.edit(request, meterBox);
    }

    /**
     * 根据设备通讯地址及班组获取其他设备的数量
     * @param protocolAddress
     * @param operationsTeam
     * @return
     */
    private int getOtherDevCnt(String protocolAddress,long operationsTeam){
        int cnt = 0;
        cnt = cnt + branchBoxRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + meterRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        cnt = cnt + transFormRepository.countAllByDelAndProtocolAddressAndOperationsTeam(0,protocolAddress,operationsTeam);
        return  cnt ;
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,4);
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
            devService.deleteDevs(ids,4);
            return ResponseUtils.okJson("删除成功",ids);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),ids);
        }
    }
}

