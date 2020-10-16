
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.DaySms;
import com.wt.mis.fi.repository.DaySmsRepository;
import com.wt.mis.sys.entity.DictItem;
import com.wt.mis.sys.repository.AccountRepository;
import com.wt.mis.sys.repository.DictItemRepository;
import com.wt.mis.sys.service.AccountService;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/fi/daysms")
public class DaySmsController extends BaseController<DaySms> {

    @Autowired
    DaySmsRepository daySmsRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public BaseRepository<DaySms, Long> repository() {
        return daySmsRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/daysms";
    }

    @Override
    protected String generateSearchSql(DaySms daySms, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.*,t2.real_name from fi_day_sms as t1 left join sys_account as t2 on t1.user_id = t2.id  where t1.del = 0 ");
        if (StringUtils.isNotEmpty(request.getParameter("userName"))) {
            sql.append(" and (t2.name like  '%" + request.getParameter("userName") + "%' or t2.real_name like '%"+request.getParameter("userName")+"%') ");
        }
        if (daySms.getReceiveGzInfo()!=null) {
            sql.append(" and t1.receive_gz_info = " + daySms.getReceiveGzInfo() );
        }
        return sql.toString();
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, DaySms daySms) {
        initDaySms(daySms);
        return super.add(request, daySms);
    }

    @Override
    @ApiOperation("提交修改对象")
    @PostMapping("/edit")
    @ResponseBody
    protected String edit(HttpServletRequest request, DaySms daySms) {
        initDaySms(daySms);
        return super.edit(request, daySms);
    }

    //用于更新id对应的名称列
    private void initDaySms(DaySms daySms){
        String pointTypeNames = "";
        List<DictItem> dictItemList = DictUtils.getDictItems("熔断器事件类型");
        for(DictItem item : dictItemList){
            String[] types = daySms.getPointTypes().split(",");
            for(String type :types){
                if(item.getItemValue().equals(type)){
                    pointTypeNames = pointTypeNames + item.getItemKey() + ",";
                }
            }
        }
        daySms.setPointTypeNames(pointTypeNames);
    }


    @Override
    @ApiOperation("提交创建对象")
    @GetMapping("/add")
    protected ModelAndView openAddPage(HttpServletRequest request, DaySms daySms) {
        ModelAndView mv = super.openAddPage(request, daySms);
        //获取所有人员
        List accountList = accountRepository.findAllByDel(0);
        mv.addObject("accountList",accountList);
        //获取关注测点
        return mv;
    }

    @Override
    @ApiOperation("打开编辑页面")
    @GetMapping("/edit")
    protected ModelAndView openEditPage(HttpServletRequest request, @NonNull Long id) {
        ModelAndView mv = super.openEditPage(request, id);
        //获取所有人员
        List accountList = accountRepository.findAllByDel(0);
        mv.addObject("accountList",accountList);
        return  mv;
    }

    @GetMapping("/getAllPointTypes")
    @ResponseBody
    public List<Node> getAllPointTypes(){
        List nodeList = new ArrayList();
        List<DictItem> list = DictUtils.getDictItems("熔断器事件类型");
        for(DictItem item:list){
            Node node = new Node(item.getItemValue(),item.getItemKey());
            nodeList.add(node);
        }
        return nodeList;
    }

    @Data
    class Node{
        Node(String id,String name){
            this.id = id;
            this.name = name;
        }
        String id;
        String name;
    }

}
