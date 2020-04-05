
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dev/line")
public class LineController extends BaseController<Line> {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<Line, Long> repository() {
        return lineRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/line";
    }

    @Override
    @ModelAttribute(name = "model")
    public void initModel(@RequestParam(value = "id", required = false) Long id, Model model, Line line) {
        super.initModel(id, model, line);
        //初始化班组为当前登录人班组
        if(line.getId()==null){
            line.setOperationsTeam(LoginUser.getCurrentUser().getDepId());
        }
    }

    @Override
    protected String generateSearchSql(Line line, HttpServletRequest request) {
        List list = DictUtils.getDictItems("电压等级");
        Dep dep = new Dep();
        if(line.getOperationsTeam()==null){
            dep = depRespository.getOne(LoginUser.getCurrentUser().getDepId());
        }else{
            dep = depRespository.getOne(Long.valueOf(line.getOperationsTeam()));
        }
        StringBuffer sql = new StringBuffer("select t1.*,t2.name as operations_team_name from dev_line as t1  left join  sys_dep t2 on t1.operations_team = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(line.getLineName())) {
            sql.append(" and t1.line_name like '%" + line.getLineName() + "%'");
        }
        if (StringUtils.isNotEmpty(line.getLineNum())) {
            sql.append(" and t1.line_num like '%" + line.getLineNum() + "%'");
        }
        sql.append(" and (t2.level like '" + dep.getLevel() + "%' or t1.operations_team is null )" );
        return sql.toString();
    }

    /**
     * 处理列表页面中要显示的数据内容
     * @param searchResultlist
     */
    @Override
    protected void dealSearchList(List searchResultlist) {
        //将字典项中的值替换成显示名称
        for(Object obj:searchResultlist){
            HashMap<String,String> map = (HashMap) obj;
            String key = DictUtils.getDictItemKey("电压等级",map.get("voltage_level"));
            map.replace("voltage_level",key);
        }
    }

    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    @Override
    protected ModelAndView openViewPage(@RequestParam("id") @NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        Line line = lineRepository.getOne(id);
        mv.addObject("voltageLevel",DictUtils.getDictItemKey("电压等级",line.getVoltageLevel()));
        mv.addObject("operationsTeam",depRespository.getOne(line.getOperationsTeam()).getName());
        return mv;
    }
}

