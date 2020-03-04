package com.wt.mis.dev.controller;

import com.wt.mis.dev.service.DevService;
import com.wt.mis.dev.view.SelectOption;
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

    @ResponseBody
    @GetMapping("dev_list")
    public List<SelectOption> getDevList(HttpServletRequest request){
        int devType = Integer.parseInt(request.getParameter("devType").trim());
        //后期如果需要按照人员结构过滤显示设备的话，增加参数
        List<SelectOption> list  = devService.getDevListForSelect(devType,null);
        return list;
    }

}
