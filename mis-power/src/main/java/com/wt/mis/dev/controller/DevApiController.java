package com.wt.mis.dev.controller;

import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.SelectOption;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dev/api")
public class DevApiController {

    @Autowired
    DevService devService;

    @Autowired
    DepRespository depRespository;


    @ResponseBody
    @GetMapping("dev_list")
    public List<SelectOption> getDevList(HttpServletRequest request){
        int devType = Integer.parseInt(request.getParameter("devType").trim());
        Long transFormId = null;
        if(StringUtils.isNotEmpty(request.getParameter("transFormId"))){
            transFormId = Long.parseLong(request.getParameter("transFormId"));
        }
        Dep dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        List<SelectOption> list  = devService.getDevListForSelect(devType,transFormId, dep.getLevel());
        return list;
    }

}
