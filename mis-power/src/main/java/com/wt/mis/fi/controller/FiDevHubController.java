
package com.wt.mis.fi.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.StringUtils;
import com.wt.mis.fi.entity.FiDevHub;
import com.wt.mis.fi.entity.FiLine;
import com.wt.mis.fi.repository.FiDevHubRepository;
import com.wt.mis.fi.repository.FiLineRepository;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.view.TreeView;
import io.swagger.annotations.ApiOperation;
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
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/fi/devhub")
public class FiDevHubController extends BaseController<FiDevHub> {

    @Autowired
    FiDevHubRepository fiDevHubRepository;
    @Autowired
    FiLineRepository fiLineRepository;

    @Override
    public BaseRepository<FiDevHub, Long> repository() {
        return fiDevHubRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "fi/devhub";
    }

    @Override
    protected String generateSearchSql(FiDevHub fiDevHub, HttpServletRequest request) {
        StringBuffer sql = new StringBuffer("select t1.* from fi_dev_hub as t1  where t1.del = 0 ");
        return sql.toString();
    }


    @Override
    @ApiOperation("提交创建对象")
    @PostMapping("/add")
    @ResponseBody
    protected String add(HttpServletRequest request, FiDevHub fiDevHub) {
        //添加线路下的节点时不用设置lineid
        if(fiDevHub.getParentId()!=0){
            FiDevHub pdev = fiDevHubRepository.getOne(fiDevHub.getParentId());
            fiDevHub.setLineId(pdev.getLineId());
        }
        return super.add(request, fiDevHub);
    }

    @Override
    @ApiOperation("打开查看页面")
    @GetMapping("/view")
    protected ModelAndView openViewPage(@NonNull Long id) {
        ModelAndView mv = super.openViewPage(id);
        FiDevHub fiDevHub = fiDevHubRepository.getOne(id);
        if(fiDevHub.getParentId()==0){
            //获取线路信息
            FiLine fiLine = fiLineRepository.getOne(fiDevHub.getLineId());
            mv.addObject("parentName",fiLine.getLineName());
        }else{
            //获取上级节点信息
            FiDevHub pfiDevHub = fiDevHubRepository.getOne(fiDevHub.getParentId());
            mv.addObject("parentName",pfiDevHub.getHubLocation()+"["+pfiDevHub.getHubTermaddr()+"]");
        }

        return mv;
    }

    @ResponseBody
    @GetMapping("/tree")
    public List<TreeView> getTreeJson(){
        List treeViewList = new ArrayList();
        List<FiLine> lineList = fiLineRepository.findAllByDel(0);
        for(FiLine line : lineList){
            TreeView lineView = new TreeView(line.getId().toString(),line.getLineName(),true,"line");
            treeViewList.add(lineView);
            lineView.setChildren(getChildHub(line));
        }
        return treeViewList;
    }

    private List<TreeView> getChildHub(FiLine line){
        List hubViewList = new ArrayList();
        //先获取线路节点下的第一层数据
        List<FiDevHub> devList = fiDevHubRepository.findAllByLineIdAndParentIdAndDel(line.getId(),0,0);
        List<FiDevHub> alldevList = fiDevHubRepository.findAllByLineIdAndDel(line.getId().intValue(),0);
        for(FiDevHub dev : devList){
            TreeView devView = new TreeView(dev.getId().toString(),dev.getHubLocation()+"["+dev.getHubTermaddr().toString()+"]",true,"dev");
            devView.setChildren(getChildHubDg(dev,alldevList));
            hubViewList.add(devView);
        }
        return hubViewList;
    }

    /**
     * 递归获取子机构
     *
     * @param dep     父机构
     * @param depList 所有机构
     * @return childMenus 所有子菜单
     */
    private List<TreeView> getChildHubDg(FiDevHub dep, List<FiDevHub> depList) {
        List<TreeView> childList = new ArrayList();
        for (FiDevHub depChild : depList) {
            if (depChild.getParentId().longValue() == dep.getId().longValue()) {
                TreeView devView = new TreeView(depChild.getId().toString(),depChild.getHubLocation()+"["+depChild.getHubTermaddr().toString()+"]",true,"dev");
                devView.setChildren(getChildHubDg(depChild, depList));
                childList.add(devView);
            }
        }
        return childList;
    }



}
