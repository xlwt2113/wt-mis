
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.repository.LineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/dev/line")
public class LineController extends BaseController<Line> {

    @Autowired
    LineRepository lineRepository;

    @Override
    public BaseRepository<Line, Long> repository() {
        return lineRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/line";
    }

    @Override
    protected String generateSearchSql(Line line, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from dev_line as t1  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(line.getLineName())) {
            sql.append(" and t1.line_name like '%" + line.getLineName() + "%'");
        }
        if (StringUtils.isNotEmpty(line.getLineNum())) {
            sql.append(" and t1.line_num like '%" + line.getLineNum() + "%'");
        }
        if (line.getOperationsTeam()!=null) {
            sql.append(" and t1.operations_team = " + line.getOperationsTeam() );
        }
        return sql.toString();
    }
}

