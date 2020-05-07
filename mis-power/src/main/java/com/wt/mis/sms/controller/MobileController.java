
package com.wt.mis.sms.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.LoginUser;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.dev.entity.Line;
import com.wt.mis.dev.entity.Topology;
import com.wt.mis.dev.entity.TransForm;
import com.wt.mis.dev.repository.LineRepository;
import com.wt.mis.dev.repository.TopologyRepository;
import com.wt.mis.dev.repository.TransFormRepository;
import com.wt.mis.sms.entity.JsonTreeModel;
import com.wt.mis.sms.entity.Mobile;
import com.wt.mis.sms.repository.MobileRepository;
import com.wt.mis.sys.entity.Account;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/sms/mobile")
public class MobileController extends BaseController<Mobile> {

    @Autowired
    MobileRepository mobileRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    TransFormRepository transFormRepository;

    @Autowired
    TopologyRepository topologyRepository;

    @Autowired
    DepRespository depRespository;

    @Autowired
    SearchService searchService;

    @Override
    public BaseRepository<Mobile, Long> repository() {
        return mobileRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sms/mobile";
    }

    @Override
    protected String generateSearchSql(Mobile mobile, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer(" select t1.*,t2.mobile,t2.real_name,t3.transform_name from sms_mobile as t1  left join sys_account t2 on t1.account_id = t2.id left join transform_dev_transform t3 on t1.transform_id = t3.id where t1.del = 0 ");
        if (StringUtils.isNotEmpty(mobile.getMobile())) {
            sql.append(" and t2.mobile like '%" + mobile.getMobile() + "%'");
        }
        if (StringUtils.isNotEmpty(mobile.getName())) {
            sql.append(" and t2.realName like '%" + mobile.getName() + "%'");
        }
        return sql.toString();
    }


    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list_user")
    @ResponseBody
    protected ResponseEntity list(HttpServletRequest request,
                                  Account account,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {

        String devId = request.getParameter("devId");
        StringBuffer sql = new StringBuffer("select t1.*,t2.`name` as dep_name from sys_account t1 left join sys_dep t2  on t1.dep_id = t2.id  where t1.del = 0 ");
        //默认查看当前登录人归属部门及下级部门的
        if(StringUtils.isNotEmpty(devId)){
            TransForm transForm = transFormRepository.getOne(Long.parseLong(devId));
            Dep dep = depRespository.getOne(transForm.getOperationsTeam());
            sql.append(" and t2.level like '" + dep.getLevel() + "%' " );
            PageResult page = searchService.findBySql(sql.toString(), pageNumber, pageSize);
            dealSearchList(page.getContent());
            return ResponseUtils.ok("", page);
        }else{
            return ResponseUtils.ok("", new PageResult());
        }
    }

    @ResponseBody
    @GetMapping("line_tree")
    public List lineTree(){
        List<Line> lineList = lineRepository.findAllByOperationsTeam(LoginUser.getCurrentUser().getDepId());
        List<JsonTreeModel> treeData = new ArrayList<>();
        for(Line line :lineList){
            JsonTreeModel jsonLine = new JsonTreeModel();
            jsonLine.setId("line_"+line.getId());
            jsonLine.setTitle(line.getLineName());
            jsonLine.setType(1);
            List<JsonTreeModel> childrens = new ArrayList<>();
            //获取该线路下的台区
            List<TransForm> transformList = transFormRepository.findAllByDelAndLineId(0,line.getId());
            for(TransForm transForm : transformList){
                JsonTreeModel children = new JsonTreeModel();
                children.setTitle(transForm.getTransformName());
                children.setId(String.valueOf(transForm.getId()));
                children.setType(2);
                childrens.add(children);
            }
            jsonLine.setChildren(childrens);
            treeData.add(jsonLine);
        }
        return treeData;
    }

    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, Mobile mobile) {
        String[] chooseAccountIds = request.getParameter("chooseAccountId").split(",");
        String chooseTransformId = request.getParameter("chooseTransformId");
        for(String chooseAccountId : chooseAccountIds){
            int cnt = this.mobileRepository.countAllByDelAndAccountIdAndTransformId(0,Long.parseLong(chooseAccountId),Long.parseLong(chooseTransformId));
            if(cnt==0){
                Mobile mobile1 = new Mobile();
                mobile1.setAccountId(Long.parseLong(chooseAccountId));
                mobile1.setTransformId(Long.parseLong(chooseTransformId));
                this.mobileRepository.save(mobile1);
            }
        }
        return ResponseUtils.okJson("新增成功", mobile);
    }
}
