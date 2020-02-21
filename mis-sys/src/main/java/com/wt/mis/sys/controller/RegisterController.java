
package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.sys.entity.Register;
import com.wt.mis.sys.repository.RegisterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/sys/register")
public class RegisterController extends BaseController<Register> {

    @Autowired
    RegisterRepository registerRepository;

    @Override
    public BaseRepository<Register, Long> repository() {
        return registerRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/register";
    }

    @Override
    protected String generateSearchSql(Register register, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from sys_register as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(register.getItemName())) {
            sql.append(" and t1.item_name like '%" + register.getItemName() + "%'");
        }
        if (StringUtils.isNotEmpty(register.getItemValue())) {
            sql.append(" and t1.item_value like '%" + register.getItemValue() + "%'");
        }
        sql.append(" order by t1.item_name");
        return sql.toString();
    }
}

