
package com.wt.mis.test.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.test.entity.TestComputer;
import com.wt.mis.test.repository.TestComputerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/test/computer")
public class TestComputerController extends BaseController<TestComputer> {

    @Autowired
    TestComputerRepository testComputerRepository;

    @Override
    public BaseRepository<TestComputer, Long> repository() {
        return testComputerRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "test/computer";
    }

    @Override
    protected String generateSearchSql(TestComputer testComputer, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from test_computer as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(testComputer.getCpu())) {
            sql.append(" and t1.cpu like '%" + testComputer.getCpu() + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("productB"))) {
            sql.append(" and t1.product >= '" + request.getParameter("productB") + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("productE"))) {
            sql.append(" and t1.product <= '" + request.getParameter("productE") + " 23:59:59'");
        }
        return sql.toString();
    }
}

