
package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.TransForm;
import com.wt.mis.sys.repository.TransFormRepository;
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
        StringBuffer sql = new StringBuffer("select t1.* from sys_trans_form as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(transForm.getTransformName())) {
            sql.append(" and t1.transform_name like '%" + transForm.getTransformName() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getTransformNum())) {
            sql.append(" and t1.transform_num like '%" + transForm.getTransformNum() + "%'");
        }
        if (StringUtils.isNotEmpty(transForm.getOperationsTeam())) {
            sql.append(" and t1.operations_team like '%" + transForm.getOperationsTeam() + "%'");
        }
        return sql.toString();
    }
}

