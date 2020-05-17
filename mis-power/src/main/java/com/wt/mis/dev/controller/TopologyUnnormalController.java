
package com.wt.mis.dev.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.dao.PageResult;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.service.SearchService;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.dev.entity.TopologyUnnormal;
import com.wt.mis.dev.repository.TopologyUnnormalRepository;
import com.wt.mis.sys.util.DictUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dev/topologyunnormal")
public class TopologyUnnormalController extends BaseController<TopologyUnnormal> {

    @Autowired
    TopologyUnnormalRepository topologyUnnormalRepository;

    @Autowired
    SearchService searchService;

    @Override
    public BaseRepository<TopologyUnnormal, Long> repository() {
        return topologyUnnormalRepository;
    }

    @Override
    protected String getUrlPrefix() {
        return "dev/topologyunnormal";
    }


    @Override
    protected String generateSearchSql(TopologyUnnormal topologyUnnormal, HttpServletRequest request) {
        StringBuffer sql  = new StringBuffer("select t1.*,t2.transform_name from transform_dev_topology_unnormal t1 " +
                " LEFT JOIN transform_dev_transform t2 on t2.id = t1.transform_id where t1.del = 0 and t2.del = 0 " +
                " and t1.transform_id = " + request.getParameter("transformId"));
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
            String key = DictUtils.getDictItemKey("设备类型",String.valueOf(map.get("dev_type")));
            map.put("dev_type_name",key);

            if("0".equals(String.valueOf(map.get("dev_online")))){
                map.replace("dev_online","在线");
            }else{
                map.replace("dev_online","<font color='red'>离线</font>");
            }

            if("0".equals(String.valueOf(map.get("dev_exist")))){
                map.replace("dev_exist","存在");
            }else{
                map.replace("dev_exist","<font color='red'>不存在</font>");
            }
        }
    }

    @ApiOperation("根据对象的属性查询所有对象")
    @PostMapping("/list_for_topology_manage")
    @ResponseBody
    protected ResponseEntity list(HttpServletRequest request,
                                  T t,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        StringBuffer sql  = new StringBuffer("select t1.*,t2.transform_name from transform_dev_topology_unnormal t1 " +
                " LEFT JOIN transform_dev_transform t2 on t2.id = t1.transform_id where t1.del = 0 and t2.del = 0 " );
        PageResult page = searchService.findBySql(sql.toString(), pageNumber, pageSize);
        dealSearchList(page.getContent());
        return ResponseUtils.ok("", page);
    }


}
