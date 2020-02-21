
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/transform")
public class TransFormController extends BaseController<TransForm> {

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<TransForm, Long> repository() {
        return transFormRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/transform";
    }

    @Override
    protected String generateSearchSql(TransForm transForm, HttpServletRequest request) {
        Dep dep = new Dep();
        if(transForm.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(transForm.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_trans_form as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(transForm.getTransformName())) {
            sql.append(" and t1.transform_name like '%" + transForm.getTransformName() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getTransformNum())) {
            sql.append(" and t1.transform_num like '%" + transForm.getTransformNum() + "%'");
        }
        sql.append(" and t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null"  );
        return sql.toString();
    }
}

