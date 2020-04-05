
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.exception.AppException;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.BranchBox;
import com.wt.mis.dev.repository.BranchBoxRepository;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dev/branchbox")
public class BranchBoxController extends BaseController<BranchBox> {

    @Autowired
    BranchBoxRepository branchBoxRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    DevService devService;

    @Override
    public BaseRepository<BranchBox, Long> repository() {
        return branchBoxRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/branchbox";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, BranchBox branchBox) {
        super.initModel(id, model, branchBox);
        //初始化班组为当前登录人班组
        if(branchBox.getId()==null){
            branchBox.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
    }

    @Override
    protected String generateSearchSql(BranchBox branchBox, HttpServletRequest request) {
        Dep dep = new Dep();
        if(branchBox.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(branchBox.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_branch_box as t1 left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(branchBox.getBranchBoxName())) {
            sql.append(" and t1.branch_box_name like '%" + branchBox.getBranchBoxName() + "%'");
        }
        if (StringUtils.isNotEmpty(branchBox.getInstallationLocation())) {
            sql.append(" and t1.installation_location like '%" + branchBox.getInstallationLocation() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    @Override
    @ApiOperation("提交删除对象-单个删除")
    @GetMapping("/delete")
    @ResponseBody
    protected String delete(@NonNull Long id) {
        try{
            devService.deleteDev(id,3);
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
            devService.deleteDevs(ids,3);
            return ResponseUtils.okJson("删除成功",ids);
        }catch(AppException e){
            return ResponseUtils.errorJson(e.getMessage().toString(),ids);
        }
    }
}

