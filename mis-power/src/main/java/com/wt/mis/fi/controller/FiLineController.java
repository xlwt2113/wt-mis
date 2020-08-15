
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.FiLine;
import com.wt.mis.fi.repository.FiLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/fi/line")
public class FiLineController extends BaseController<FiLine> {

    @Autowired
    FiLineRepository fiLineRepository;

    @Override
    public BaseRepository<FiLine, Long> repository() {
        return fiLineRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/line";
    }

    @Override
    protected String generateSearchSql(FiLine fiLine, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fi_line as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(fiLine.getLineName())) {
            sql.append(" and t1.line_name like '%" + fiLine.getLineName() + "%'");
        }
        return sql.toString();
    }


}
