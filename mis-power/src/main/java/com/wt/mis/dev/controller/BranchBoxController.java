
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.BranchBox;
import com.wt.mis.dev.repository.BranchBoxRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/branchbox")
public class BranchBoxController extends BaseController<BranchBox> {

    @Autowired
    BranchBoxRepository branchBoxRepository;

    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<BranchBox, Long> repository() {
        return branchBoxRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/branchbox";
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
}

