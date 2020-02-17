package com.wt.mis.sys.controller;

import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {

    @GetMapping("/test")
    public ModelAndView test1() {
        return new ModelAndView("test");

    }

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity search(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object t,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                 @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize) {
        String sql = "select t1.name,t1.real_name,t1.sex,t1.status,t2.`name` as dep_name from sys_account t1 left join sys_dep t2  on t1.dep_id = t2.id ";
        PageResult page = searchService.findBySql(sql, pageNumber, pageSize);
        return ResponseUtils.ok("", page);
    }




}
