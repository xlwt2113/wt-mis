package com.wt.mis.test.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.test.entity.TestBook;
import com.wt.mis.test.repository.TestBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/test/book")
public class TestBookController extends BaseController<TestBook> {

    @Autowired
    TestBookRepository testBookRepository;

    @Override
    public BaseRepository<TestBook, Long> repository() {
        return testBookRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "test/book";
    }

    @Override
    protected String generateSearchSql(TestBook testBook, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from test_book as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(testBook.getBookName())) {
            sql.append(" and t1.book_name like '%" + testBook.getBookName() + "%'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("beginDateB"))) {
            sql.append(" and t1.begin_date >= '" + request.getParameter("beginDateB") + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getParameter("beginDateE"))) {
            sql.append(" and t1.begin_date <= '" + request.getParameter("beginDateE") + " 23:59:59'");
        }

        return sql.toString();
    }
}
