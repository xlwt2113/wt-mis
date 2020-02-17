package com.wt.mis.sys.controller;

import com.wt.mis.core.controller.BaseController;
import com.wt.mis.core.repository.BaseRepository;
import com.wt.mis.core.util.ResponseUtils;
import com.wt.mis.sys.entity.Dep;
import com.wt.mis.sys.repository.DepRespository;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/sys/dep")
public class DepController extends BaseController<Dep> {

    @Autowired
    DepRespository depRespository;

    @Override
    public BaseRepository<Dep, Long> repository() {
        return depRespository;
    }

    @Override
    protected String getUrlPrefix() {
        return "sys/dep";
    }


    @ApiOperation("删除机构")
    @GetMapping("/delete")
    @ResponseBody
    @Override
    protected String delete(@RequestParam("id") @NonNull Long id) {
        if (id != 0) {
            repository().deleteByIdOnLogic(id);
            return ResponseUtils.okJson("删除成功",id);
        } else {
            return ResponseUtils.errorJson("删除失败，根机构不允许删除",id);
        }
    }

    @ApiOperation("显示所有机构")
    @PostMapping("/list")
    @ResponseBody
    @Override
    protected ResponseEntity list(HttpServletRequest request,
                                  Dep dep,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageCurrent,
                                  @RequestParam(value = "limit", required = false, defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "pageSort", required = false, defaultValue = "id") String pageSort,
                                  @RequestParam(value = "pageDirection", required = false, defaultValue = "DESC") String pageDirection) {
        //获取根机构
        Dep root = depRespository.findById(new Long(1)).get();
        //获取所有没有删除的机构
        List<Dep> depList = depRespository.findAllByDel(0);
        root.setChildren(getChildDep(root, depList));
        //将整理好的树形结构放入list中用于前台展示
        List<Dep> list = new ArrayList<Dep>();
        list.add(root);
        return ResponseUtils.ok("", list);
    }

    /**
     * 递归获取子机构
     *
     * @param dep     父机构
     * @param depList 所有机构
     * @return childMenus 所有子菜单
     */
    private List<Dep> getChildDep(Dep dep, List<Dep> depList) {
        List<Dep> childList = new ArrayList<Dep>();
        for (Dep depChild : depList) {
            if (depChild.getPid().longValue() == dep.getId().longValue()) {
                depChild.setChildren(getChildDep(depChild, depList));
                childList.add(depChild);
            }
        }
        childList.sort(new Comparator<Dep>() {
            @Override
            public int compare(Dep o1, Dep o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return childList;
    }
}
